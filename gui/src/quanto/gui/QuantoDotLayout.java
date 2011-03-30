/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package quanto.gui;

import edu.uci.ics.jung.contrib.algorithms.layout.AbstractDotBangBoxLayout;
import quanto.core.BasicBangBox;
import quanto.core.BasicEdge;
import quanto.core.RGGraph;
import quanto.core.RGVertex;

/**
 *
 * @author alex
 */
public class QuantoDotLayout extends AbstractDotBangBoxLayout<RGVertex, BasicEdge, BasicBangBox> {
	public static final double PADDING = 23.0;

	public QuantoDotLayout(RGGraph graph) {
		super(graph, PADDING);
	}

	@Override
	protected String getVertexDotKey(RGVertex vertex) {
		return vertex.getCoreName();
	}
}