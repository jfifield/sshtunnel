/*
 * Copyright 2007 Joseph Fifield
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.programmerplanet.sshtunnel.ui;

import java.awt.Component;
import java.awt.Container;

import javax.swing.Spring;
import javax.swing.SpringLayout;

/**
 * A utility class for creating a grid based layout using SpringLayout.
 * 
 * Based on <a href="http://java.sun.com/docs/books/tutorial/uiswing/examples/layout/SpringGridProject/src/layout/SpringUtilities.java">SpringUtilities.java</a>.
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class SpringGrid {

	private Container container;
	private int rows;
	private int cols;
	private int verticalSpacing = 6;
	private int horizontalSpacing = 6;
	private SpringLayout layout;

	/**
	 * Constructs a new SpringGrid.
	 *
	 * @param container The parent container.
	 * @param rows The number of rows in the grid.
	 * @param cols The number of columns in the grid.
	 */
	public SpringGrid(Container container, int rows, int cols) {
		this.container = container;
		this.rows = rows;
		this.cols = cols;
		this.layout = new SpringLayout();
		this.container.setLayout(layout);
	}

	/**
	 * Constructs a new SpringGrid.
	 *
	 * @param container The parent container.
	 * @param rows The number of rows in the grid.
	 * @param cols The number of columns in the grid.
	 * @param verticalSpacing The vertical spacing between cells.
	 * @param horizontalSpacing The horizontal spacing between cells.
	 */
	public SpringGrid(Container container, int rows, int cols, int verticalSpacing, int horizontalSpacing) {
		this(container, rows, cols);
		this.verticalSpacing = verticalSpacing;
		this.horizontalSpacing = horizontalSpacing;
	}

	/**
	 * Aligns and sizes all cells in the grid and sizes the container.
	 */
	public void update() {
		Spring x = updateColumns();
		Spring y = updateRows();
		updateContainer(x, y);
	}

	/**
	 * Aligns and sizes all columns in the grid. 
	 */
	private Spring updateColumns() {
		Spring x = Spring.constant(verticalSpacing);
		for (int col = 0; col < cols; col++) {
			x = updateColumn(x, col);
		}
		return x;
	}

	/**
	 * Aligns and sizes the specified column.
	 */
	private Spring updateColumn(Spring x, int col) {
		Spring width = getColumnWidth(col);
		for (int row = 0; row < rows; row++) {
			SpringLayout.Constraints constraints = getConstraints(row, col);
			constraints.setX(x);
			constraints.setWidth(width);
		}
		x = Spring.sum(x, Spring.sum(width, Spring.constant(verticalSpacing)));
		return x;
	}

	/**
	 * Determines the width of the column by finding the maximum width of all cells in the column.
	 */
	private Spring getColumnWidth(int col) {
		Spring width = Spring.constant(0);
		for (int row = 0; row < rows; row++) {
			SpringLayout.Constraints constraints = getConstraints(row, col);
			width = Spring.max(width, constraints.getWidth());
		}
		return width;
	}

	/**
	 * Aligns and sizes all rows in the grid. 
	 */
	private Spring updateRows() {
		Spring y = Spring.constant(horizontalSpacing);
		for (int row = 0; row < rows; row++) {
			y = updateRow(y, row);
		}
		return y;
	}

	/**
	 * Aligns and sizes the specified row.
	 */
	private Spring updateRow(Spring y, int row) {
		Spring height = getRowHeight(row);
		for (int col = 0; col < cols; col++) {
			SpringLayout.Constraints constraints = getConstraints(row, col);
			constraints.setY(y);
			constraints.setHeight(height);
		}
		y = Spring.sum(y, Spring.sum(height, Spring.constant(horizontalSpacing)));
		return y;
	}

	/**
	 * Determines the height of the row by finding the maximum height of all cells in the row.
	 */
	private Spring getRowHeight(int row) {
		Spring height = Spring.constant(0);
		for (int col = 0; col < cols; col++) {
			SpringLayout.Constraints constraints = getConstraints(row, col);
			height = Spring.max(height, constraints.getHeight());
		}
		return height;
	}

	/**
	 * Sizes the container.
	 */
	private void updateContainer(Spring x, Spring y) {
		SpringLayout.Constraints constraints = layout.getConstraints(container);
		constraints.setConstraint(SpringLayout.EAST, x);
		constraints.setConstraint(SpringLayout.SOUTH, y);
	}

	/**
	 * Gets the constraints for a specific cell in the grid.
	 */
	private SpringLayout.Constraints getConstraints(int row, int col) {
		int index = row * cols + col;
		Component component = container.getComponent(index);
		SpringLayout.Constraints constraints = layout.getConstraints(component);
		return constraints;
	}

}
