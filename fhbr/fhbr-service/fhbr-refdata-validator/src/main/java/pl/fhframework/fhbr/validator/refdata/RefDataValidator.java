/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.fhframework.fhbr.validator.refdata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pl.fhframework.fhbr.api.exception.XmlParserException;
import pl.fhframework.fhbr.api.service.ValidationResult;
import pl.fhframework.fhbr.validator.refdata.checker.RefDataChecker;
import pl.fhframework.fhbr.validator.refdata.checker.RefDataInaccessibilityRuntimeException;
import pl.fhframework.fhbr.validator.refdata.list.RefDataList;
import pl.fhframework.fhbr.validator.refdata.list.RefDataListBuilder;


public class RefDataValidator {
	private final static Logger log = LoggerFactory.getLogger(RefDataValidator.class);

	RefDataValidationResult validationResult;
    private RefDataChecker refDataSource;
    private RefDataListBuilder refDataListBuilder;
    private String messageType;
    private byte[] messageContent;
    private Document xmlDocument;
    private LocalDateTime date;
    private XPathFactory xpathFactory = XPathFactory.newInstance();
    
    
    
    

    public RefDataValidator(RefDataChecker refDataSource, RefDataListBuilder refDataListBuilder,
    		String namespace, byte[] messageContent) {
		super();
		this.refDataSource = refDataSource;
		this.refDataListBuilder = refDataListBuilder;
		this.messageContent = messageContent;
		this.messageType = namespace;

        try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			this.xmlDocument = db.parse(new ByteArrayInputStream(messageContent));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new XmlParserException(e);
		} 
		
	}
    
    
    

	public RefDataValidator(RefDataChecker refDataSource, RefDataListBuilder refDataListBuilder, String messageType,
			Document document, LocalDateTime date) {
		super();
		this.refDataSource = refDataSource;
		this.refDataListBuilder = refDataListBuilder;
		this.messageType = messageType;
		this.xmlDocument = document;
		this.date = date;
	}




	public void validate() {
        this.validate(null);
    }

    public void validate(RefDataList refDataList) {

        String resultText = "";
        try {
             if (refDataList == null) {
                 refDataList = refDataListBuilder.createRefDataList(messageType);

             }
 			Node rootNode = xmlDocument.getDocumentElement();
 			Map<Node,String> pathsPath = new HashMap<Node,String>();

            
            for (String code : refDataList.getCodeList()) {

                Map<String,List<Node>> value2Pointer = new HashMap<String,List<Node>>();
                
                for (String xpath : refDataList.getXPathList(code)) {
                    NodeList nodelist = null;
                    nodelist = selectNodeList(rootNode, xpath);


                    for (int n = 0; n < nodelist.getLength(); n++) {

                        Node node = nodelist.item(n);

                        String value = getNodeValue(node);
                        if(value == null) {
                            if (log.isWarnEnabled()) log.warn("value was null for node:" +evaluateXPath(pathsPath,node));
                            continue; 
                        }


                        if(refDataSource == null){
                            String path = evaluateXPath(pathsPath,node);
                            validationResult.addMessage("L", code, path, "[RefDataValidator] Invalid reference data - there is no reference data for this field.", value);
                        } else {
                            if(value2Pointer.get(value) == null){
                                value2Pointer.put(value, new LinkedList<Node>());
                            }
                            value2Pointer.get(value).add(node);
                        }
                    }
                }

                for (Map.Entry<String, List<Node>> entry : value2Pointer.entrySet()) {
                       String value = entry.getKey();
                       try{
                            if (!refDataSource.isValid(code, value, date)) {
                                for (Node node : entry.getValue()) {
                                    String path = evaluateXPath(pathsPath,node);
                                    validationResult.addMessage("L", code, path, "[RefDataValidator] Invalid value '" + value + "' for reference data with code " + code + ".", value);
                                }
                            }
                        }catch(RefDataInaccessibilityRuntimeException ie){
                            for (Node node : entry.getValue()) {
                                String path = evaluateXPath(pathsPath,node);
                                validationResult.addMessage("L", code, path, "[RefDataValidator] Invalid value '" + value + "' for reference data with code " + code + ".", value);
                           }
                        }
                }

            }

        }
        catch (RefDataInaccessibilityRuntimeException e) {
            log.error(">>>>>>>>[RefDataInaccessibilityRuntimeException]"+e.getMessage(),e );
            validationResult.setResult(-3, e.getMessage());
        }
        catch (Throwable e) {
            log.error(">>>>>>>>[Throwable]"+ e.getMessage(),e );
            validationResult.setResult(-99, e.getMessage());
        }


    }

    public NodeList selectNodeList(Node contextNode, String expression) throws XPathExpressionException {
        XPath xpath = xpathFactory.newXPath();
        XPathExpression xexpr = xpath.compile(expression);
        return (NodeList) xexpr.evaluate(contextNode, XPathConstants.NODESET);
	}
    
    public String evaluateXPath(Map<Node, String> pathParts, Node node) {
        if ( node == null) return null;
//      if ( node.getNodeType() != Node.ATTRIBUTE_NODE && node.getNodeType() != Node.ELEMENT_NODE && node.getNodeType() != Node.DOCUMENT_NODE)
//          throw new ToolException("Invalid node type '" +node.getNodeType()+ "'");
      String xpath = "";
      if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
          xpath = "/@" + node.getNodeName();
          node = ((Attr)node).getOwnerElement();
      }
      do {
          if (node.getNodeType() == Node.DOCUMENT_NODE) break;
          if (node.getNodeType() == Node.ELEMENT_NODE) {
              int pos = 1;
              if(pathParts.containsKey(node)){
                  xpath = pathParts.get(node) + xpath;
//                  if (log.isDebugEnabled()) log.debug("taking cached part for node: " + pathParts.get(node));
              }else{
                  try {
                          long sTime, eTime;
                          //sTime = System.currentTimeMillis();
                      NodeList nodeList = selectNodeList(node, "preceding-sibling::" + node.getNodeName() );
                      //eTime = System.currentTimeMillis();
                      //System.out.println("selectNodeList time: " + (eTime - sTime)/1000.0 + " [s]" );
                      if (nodeList != null) pos += nodeList.getLength();

                      //sTime = System.currentTimeMillis();
                      //XPathAPI.eval(node, "count(preceding-sibling::" + node.getNodeName() +")");
                      //eTime = System.currentTimeMillis();
                      //System.out.println("selectNodeList time2: " + (eTime - sTime)/1000.0 + " [s]" );


                  } catch (XPathExpressionException ex) {
                	  ex.printStackTrace();
                  }

                      pathParts.put(node, "/" + node.getNodeName() + "[" + pos + "]");
//                      if (log.isDebugEnabled()) log.debug("caching part for node: " + pathParts.get(node));
                  //if (pos > 1) {
                      xpath = "/" + node.getNodeName() + "[" + pos + "]" + xpath;
                  /*} else {
                      xpath = "/" + node.getNodeName() + xpath;
                  }*/
              }
              node = node.getParentNode();
          }

      } while (true);

      return xpath;
	}
    
    public static final String getNodeValue(Node node) {
        if (node != null) {
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    NodeList nodelist = node.getChildNodes();
                    if (nodelist != null) {
                        for (int i = 0; i < nodelist.getLength(); i++) {
                            if (nodelist.item(i).getNodeType() == Node.TEXT_NODE) {
                                return nodelist.item(i).getNodeValue();
                            }
                        }
                    }
                    break;
                case Node.ATTRIBUTE_NODE:
                    return node.getNodeValue();
                case Node.TEXT_NODE:
                    return node.getTextContent();
            }
        }
        return null;
    }    
    
	public ValidationResult getValidationResult() {
        return validationResult;
    }

    public void setRefDataListBuilder(RefDataListBuilder refDataListBuilder) {
        this.refDataListBuilder = refDataListBuilder;
    }

    public RefDataListBuilder getRefDataListBuilder() {
            return this.refDataListBuilder;
      }

}
