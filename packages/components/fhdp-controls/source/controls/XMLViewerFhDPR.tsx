import * as React from 'react';
import XMLViewer from 'react-xml-viewer'

interface Props {
    content: string;
}

export const XMLViewerFhDPR: React.FC<Props> = (props: Props) => {
    const {content} = props;

    const [xml, setXml] = React.useState(content);

    React.useEffect(() => {
        setXml(content);
    },
    [content]);

    return (<XMLViewer collapsible={true} theme={{attributeValueColor: 'var(--color-black)', textColor: 'var(--color-black)'}} xml={xml}/>)
}
