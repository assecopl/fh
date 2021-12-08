package pl.fhframework.dp.commons.utils.xml;
/*
 * NodeHelper.java
 *
 * Prawa autorskie do oprogramowania i jego kodów źródłowych
 * przysługują w pełnym zakresie wyłącznie SKG S.A.
 *
 * All copyrights to software and its source code
 * belong fully and exclusively to SKG S.A.
 */

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Dariusz Skrudlik
 * @version $Revision: 126 $, $Date: 2009-10-29 23:11:46 +0100 (Cz, 29 paź 2009) $
 */
public class NodeHelper {

    /** Creates a new instance of NodeHelper */
    public NodeHelper() {
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

    /**
     * Uproszczona wersja metody selectSingleNode z klasy XPathAPI. Obsługuje
     * wyłącznie proste odwołania do węzłów i atrybótów. xpath musi być podany
     * względem przekazanego węzła
     *
     * @param node_context -
     *            węzeł względem którego ma być rozpatrywany xpath
     * @param context_xpath -
     *            xpath do żądanego węzła
     * @return - zwraca żądany węzeł
     */
    public static final Node selectSingleNode(final Node node_context, String context_xpath) {
        Node node = null;
        if (context_xpath != null && context_xpath.length() > 0) {
            if (context_xpath == null || context_xpath.contains("[") || context_xpath.contains("@") || context_xpath.contains("//")) {
                throw new RuntimeException("complicated xpath: " + context_xpath);
            }

            if (node_context.getParentNode().getNodeType() == Node.DOCUMENT_NODE && context_xpath.startsWith("/") ) {
                context_xpath = context_xpath.substring(context_xpath.indexOf("/", 2));
            }

            StringTokenizer st = new StringTokenizer(context_xpath, "/");
            Node nextnode = node_context;
            while (st.hasMoreTokens() && nextnode != null) {
                String xpath_token = st.nextToken();

                nextnode = nextnode.getFirstChild();
                if (nextnode != null) {
                    do {
                        if (nextnode.getNodeType() == Node.ELEMENT_NODE && xpath_token.equals(nextnode.getNodeName())) {
                            if (!st.hasMoreTokens()) {
                                return nextnode;
                            }
                            break;
                        }
                        nextnode = nextnode.getNextSibling();
                    } while (nextnode != null);
                }
            }

        }

        return null;
    }

    /**
     * Uproszczona wersja metody selectNodeList z klasy XPathAPI. Obsługuje
     * wyłącznie proste odwołania do węzłów. Xpath musi być podany
     * względem przekazanego węzła i może zewierać tylko jeden element powielarny na ostatnim miejscu.
     */
    public static final List<Node> selectNodeList(final Node node_context, String context_xpath) {
        ArrayList<Node> list = new ArrayList<Node>();
        Node node = null;
        if (context_xpath != null && context_xpath.length() > 0) {
            if (context_xpath == null || context_xpath.contains("[") || context_xpath.contains("@") || context_xpath.contains("//") ) {
                throw new RuntimeException("complicated xpath: " + context_xpath);
            }

            if (node_context.getParentNode().getNodeType() == Node.DOCUMENT_NODE && context_xpath.startsWith("/")) {
                context_xpath = context_xpath.substring(context_xpath.indexOf("/", 2));
            }

            StringTokenizer st = new StringTokenizer(context_xpath, "/");
            Node nextnode = node_context;
            while (st.hasMoreTokens() && nextnode != null) {
                String xpath_token = st.nextToken();

                nextnode = nextnode.getFirstChild();
                if (nextnode != null) {
                    do {
                        if (nextnode.getNodeType() == Node.ELEMENT_NODE && xpath_token.equals(nextnode.getNodeName())) {
                            if (st.hasMoreTokens()) {
                                break;
                            }
                            list.add(nextnode);
                        }
                        nextnode = nextnode.getNextSibling();
                    } while (nextnode != null);
                }
            }
        }

        return list;
    }

   /**
     * Return used prefix for namespace in current document.
     * Prefix is end by ':' if namespace is default then ony ':' is returned.
     * If document not contains namespaces or namespaceAware is false then empty string is returned.
     * Should be used for document with not default namespaces. \
     * e.g.:
     * &lt;tns:IE315LT ...
     *    &lt;tns:GoodsItem
     *    &lt;tns:ManifestItem
     *
     * usage:
     * XMLHelper xml = new XMLHelper(true); //turn on namespaces
     * String prefix = NodeHelper.getElementPrefix(xml.getDocument().getDocumentElement());
     * xml.selectSingleNode("//" + prefix + "GoodsItem|//" + prefix + "ManifestItem"));
     *
     * @param element - xmle element
     * @return prefix ended by ':' if exists
     */
    public static String getElementPrefix(Element element) {
        String ls_ret = null;
        if (element != null) {
            ls_ret = element.getPrefix();
            if (ls_ret == null) {
                String namespaceURI = element.getNamespaceURI();
                if (namespaceURI != null) {
                    ls_ret = element.lookupPrefix(namespaceURI);
                    if (ls_ret==null) {
                        ls_ret = ""; //default namespace
                    }
                }
            }
        }
        ls_ret = (ls_ret == null ? ls_ret = "" : ls_ret + ":");
        return ls_ret;
    }
}
