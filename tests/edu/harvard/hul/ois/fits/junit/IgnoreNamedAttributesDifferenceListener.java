package edu.harvard.hul.ois.fits.junit;

import java.util.HashSet;
import java.util.Set;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.w3c.dom.Node;

public class IgnoreNamedAttributesDifferenceListener implements DifferenceListener {
    private Set<String> blackList = new HashSet<String>();

    public IgnoreNamedAttributesDifferenceListener(String ... elementNames) {
        for (String name : elementNames) {
            blackList.add(name);
        }
    }

    @Override
    public int differenceFound(Difference difference) {
        if (difference.getId() == DifferenceConstants.ATTR_VALUE_ID) {
            if (blackList.contains(difference.getControlNodeDetail().getNode().getNodeName())) {
                return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
            }
        }
        return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
    }

    public void skippedComparison(Node node, Node node1) {

    }
}