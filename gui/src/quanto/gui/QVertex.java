package quanto.gui;

import java.awt.Color;

import edu.uci.ics.jung.contrib.HasName;

public class QVertex implements HasName, Comparable<QVertex> {
	public enum Type { RED, GREEN, BOUNDARY, HADAMARD };
	private Type vertexType;
	private String name, angle;
	public boolean old;

	public QVertex() {
		this(null);
	}
	
	public QVertex(Type vertexType) {
		this.vertexType = vertexType;
		this.old = false;
	}

	public Type getVertexType() {
		return vertexType;
	}

	public void setVertexType(Type vertexType) {
		this.vertexType = vertexType;
	}
	
	public void setVertexType(String vertexType) {
		vertexType = vertexType.toLowerCase();
		if (vertexType.equals("red"))
			setVertexType(QVertex.Type.RED);
		else if (vertexType.equals("green"))
			setVertexType(QVertex.Type.GREEN);
		else if (vertexType.equals("h"))
			setVertexType(QVertex.Type.HADAMARD);
		else throw new IllegalArgumentException("vertexType");
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return getAngle().replace('\\', 'B')+"    ";
	}
	
	public Color getColor() {
		if (vertexType==Type.RED) return Color.red;
		if (vertexType==Type.GREEN) return Color.green;
		if (vertexType==Type.HADAMARD) return Color.yellow;
		return Color.lightGray;
	}
	
	public void updateTo(QVertex v) {
		old = false;
		name = v.getName();
		vertexType = v.getVertexType();
		angle = v.getAngle();
	}

	public String getAngle() {
		return angle;
	}

	public void setAngle(String angle) {
		this.angle = angle;
	}
	
	public boolean isAngleVertex() {
		return (vertexType == Type.RED || vertexType == Type.GREEN);
	}

	public int compareTo(QVertex o) {
		return getName().compareTo(o.getName());
	}
	
}