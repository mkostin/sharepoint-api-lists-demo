package com.example.office.utils;

import java.util.ArrayList;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.example.office.logger.Logger;


/**
 * XML utility helper class.
 */
public class XmlUtils {

    /**
     * Retrieves attribute value with specified name from XML node, if attribute
     * wasn't found then returned null.
     *
     * @param node XML node.
     * @param attributeName Attribute name.
     *
     * @return Attribute value or null if attribute wasn't found.
     */
    public static String getAttributeValue(Node node, String attributeName) {
        try {
            return getAttributeValue(node, attributeName, null);
        } catch (final Exception e) {
        }
        return null;
    }

    /**
     * Retrieves attribute value with specified name from XML node, if attribute
     * not found then returned default value.
     *
     * @param node XML node.
     * @param attributeName Attribute name.
     * @param defaultValue Default value.
     *
     * @return Attribute value or default value if attribute wasn't found.
     */
    public static String getAttributeValue(Node node, String attributeName, String defaultValue) {
        try {
            if (node == null || attributeName == null || attributeName.length() == 0) {
                return defaultValue;
            }

            NamedNodeMap attributes = node.getAttributes();

            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.getNamedItem(attributeName);
                if (attribute != null) {
                    return attribute.getNodeValue();
                }
            }
        } catch (final Exception e) {
            Logger.logApplicationException(e, XmlUtils.class.getSimpleName() + ".getAttributeValue(): Failed.");
        }

        return defaultValue;
    }

    /**
     * Retrieves child node by specified name or null if node wasn't found.
     *
     * @param node XML node.
     * @param childNodeName Child node name.
     *
     * @return Found child node, or null if child node wasn't found.
     */
    public static Node getChildNode(Node node, String childNodeName) {
        try {
            if (node == null || childNodeName == null || childNodeName.length() == 0) {
                return null;
            }

            NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                if (childNode != null && childNode.getNodeName().equalsIgnoreCase(childNodeName)) {
                    return childNode;
                }
            }
        } catch (final Exception e) {
            Logger.logApplicationException(e, XmlUtils.class.getSimpleName() + ".getChildNode(): Failed.");
        }

        return null;
    }

    /**
     * Retrieves child node value by specified child node name or null if
     * anything is wrong.
     *
     * @param node XML node.
     * @param childNodeName Child node name.
     *
     * @return Child node value or null if anything is wrong.
     */
    public static String getChildNodeValue(Node node, String childNodeName) {
        try {
            return getNodeValue(getChildNode(node, childNodeName));
        } catch (final Exception e) {
            Logger.logApplicationException(e, XmlUtils.class.getSimpleName() + ".getChildNodeValue(): Failed.");
        }

        return null;
    }

    /**
     * Retrieves node value or null if node empty.
     *
     * @param node XML node.
     *
     * @return Node value or null if node empty.
     */
    public static String getNodeValue(Node node) {
        try {
            return getNodeValue(node, null);
        } catch (final Exception e) {
            Logger.logApplicationException(e, XmlUtils.class.getSimpleName() + ".getNodeValue(): Failed.");
        }

        return null;
    }

    /**
     * Retrieves node value or default value if node is empty.
     *
     * @param node XML node.
     * @param defaultValue Default value.
     *
     * @return Node value or default value if node is empty.
     */
    public static String getNodeValue(Node node, String defaultValue) {
        if (node == null) {
            return defaultValue;
        }

        try {
            NodeList childs = node.getChildNodes();
            for (int i = 0; i < childs.getLength(); i++) {
                Node child = childs.item(i);
                if (child.getNodeType() == Node.TEXT_NODE) {
                    return child.getNodeValue();
                }
            }
        } catch (final Exception e) {
            Logger.logApplicationException(e, XmlUtils.class.getSimpleName() + ".getNodeValue(): Failed.");
        }

        return defaultValue;
    }

    /**
     * Retrieves child nodes by specified name or null if nodes weren't found.
     *
     * @param node XML node.
     * @param childNodeName Child nodes name.
     *
     * @return Found child nodes, or null if child nodes weren't found.
     */
    public static Node[] getChildNodes(Node node, String childNodeName) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        try {
            if (node == null || childNodeName == null || childNodeName.length() == 0) {
                return null;
            }

            NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                if (childNode != null && childNode.getNodeName().equalsIgnoreCase(childNodeName)) {
                    nodes.add(childNode);
                }
            }
            if (nodes.size() > 0) {
                Node[] nodesArray = new Node[nodes.size()];
                return nodes.toArray(nodesArray);
            }
        } catch (final Exception e) {
            Logger.logApplicationException(e, XmlUtils.class.getSimpleName() + ".getChildNodes(): Failed.");
        }

        return null;
    }

    /**
     * Escapes characters for text appearing as XML data, between tags.
     *
     * @param text Text to check.
     *
     * @return Text with escaped chars.
     */
    public static String escapeForXML(String text) {
        if (text == null || text.length() <= 0) {
            return text;
        }

        final StringBuffer result = new StringBuffer();

        try {
            char character;
            int size = text.length();
            for (int i = 0; i < size; i++) {
                character = text.charAt(i);

                if (character == '<') {
                    result.append("&lt;");
                } else if (character == '>') {
                    result.append("&gt;");
                } else if (character == '\"') {
                    result.append("&quot;");
                } else if (character == '\'') {
                    result.append("&#39;");
                } else if (character == '&') {
                    result.append("&amp;");
                } else {
                    result.append(character);
                }
            }
        } catch (final Exception e) {
            Logger.logApplicationException(e, XmlUtils.class.getSimpleName() + ".escapeForXML(): Failed.");
        }

        return result.toString();
    }
}
