import * as React from 'react';
import XMLViewer from 'react-xml-viewer'
import {Parser, Builder} from 'xml2js';

interface Props {
    content: string;
}

function toLowerCase(name: string){
  return name.toLowerCase();
}

const parseErrorMessage = (message: string) => {
  const [msg, line, column, character] = message.split('\n');
  return {
    msg,
    line: Number((line.split('Line: ')[1] || '-1')),
    column: Number((column.split('Column: ')[1] || '-1')),
    character: character.split('Char: ')[1]
  }
}

const printError = (err: string, data?: string) => {
  const parsedMessage = parseErrorMessage(err);
  let wholeLine: JSX.Element | string | undefined = undefined;
  if (data) {
    let preLine = data.split('\n')[parsedMessage.line - 1];
    let pre2Line = data.split('\n')[parsedMessage.line - 2];
    let postLine = data.split('\n')[parsedMessage.line + 1];
    wholeLine = data.split('\n')[parsedMessage.line]
    const char = wholeLine[parsedMessage.column - 1];
    wholeLine = <pre>
      {pre2Line ? <>{pre2Line}<br/></> : ''}
      {preLine ? <>{preLine}<br/></> : ''}
      {wholeLine.substring(0, parsedMessage.column - 1)}
      <span style={{color: 'red'}}>{char}</span>
      {wholeLine.substring(parsedMessage.column + 1, wholeLine.length)}<br/>
      <span style={{color: 'red'}}>{'-'.repeat(parsedMessage.column - 1)}^</span><br/>
      {postLine ? <>{postLine}</> : ''}
    </pre>;
  }
  return (
    <span style={{padding: '10px'}}>
      {parsedMessage.msg}<br/>
      {wholeLine ? (<>Whole line: <span style={{color: 'orange'}}>{wholeLine}</span><br/></>) : ''}
      Line: <span style={{color: 'red'}}>{parsedMessage.line}</span><br/>
      Column: <span style={{color: 'red'}}>{parsedMessage.column}</span><br/>
      Character: <span style={{color: 'red'}}>"{parsedMessage.character}"</span><br/>
    </span>
  );
}

export const XMLViewerFhDPR: React.FC<Props> = (props: Props) => {
    const {content} = props;

    const [rawXml, setRawXml] = React.useState<string | undefined>(content);
    const [xml, setXml] = React.useState<string | undefined>();
    const [error, setError] = React.useState<string | undefined>();

    const initContent = async () => {
      try {
        const xmlParser = () => new Promise<any>((resolve, reject) => new Parser({
          valueProcessors: [
            (value: string, name: string) => {
              if (value.includes(`</${name}>`)) {
                value = value.replace(`</${name}>`, '');
              }
              if (value.includes(`<${name}>`)) {
                value = value.replace(`<${name}>`, '');
              }
              return value
                .replace(/&amp;/g, "&")
                .replace(/&lt;/g, "<")
                .replace(/&gt;/g, ">").trim();
            },
          ],
          tagNameProcessors: [toLowerCase],
          attrNameProcessors: [toLowerCase],
          strict: false
        }).parseString(
            content,
            (err: Error, result: any) => {
              if (err) {
                reject(err);
              } else {
                resolve(result);
              }
            }
          )
        );
        const json = await xmlParser();
        if (!json) {
          throw new Error('XML is empty or unparsable!');
        }

        const reparsedXML = new Builder().buildObject(json)
        setXml(reparsedXML);
      } catch (e) {
        console.log(e);
        setError((e as Error).message)
      }
    }

    React.useEffect(() => {
      setRawXml(content)
    },
    [content]);

    React.useEffect(() => {
      if (rawXml) {
        initContent();
      }
    },
    [rawXml]);




    if (!xml && !error) {
      return <span style={{padding: '10px'}}>Processing...</span>
    }
    if (error) {
      return <>{printError(error, rawXml)}</>
    }
    return (<XMLViewer collapsible={true} theme={{attributeValueColor: 'var(--color-black)', textColor: 'var(--color-black)'}} xml={xml}/>)
}
