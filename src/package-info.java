/**
 * Contains the frames used to make up the interface of the entire application, including the main application frame,
 * the Settings window and the Attribute Table window.
 */
package application_frames;

/**
 * Contains definitions of the atomic elements used to make up the functionality of the application, including Features
 * and Layers.
 */
package core_classes;

/**
 *
 */
package core_components;

/**
 * Contains extensions of default Java components that are customized for this application, indcluding an extension of
 * JFrame (CustomJFrame) and JPanel (CustomJPanel)
 */
package custom_components;

/**
 * Contains the defintion of a PostgreSQL database connection class, which is used to read and write layers to a
 * database representation.  Includes a single class (DatabaseConnection)
 */
package database;

/**
 * Contains definitions of various aesthetic effects used to supplement the application, including grid lines to be
 * displayed on the canvas (GridLine), ?????????????
 */
package effects;

/**
 * Contains definitions of the individual feature types supported by the application, points (PointItem), polylines
 * (PolylineItem) and polygons (PolygonItem).  Each of these classes are specializations of the Feature class from the
 * core_classes package.
 */
package features;

/**
 * Contains images used for icons seen on the buttons & toolbars.
 */
package images;

/**
 * Contains defintions of the table model objects used to control the tabular data structures in the application.
 * Includes the table of contents (LayerTableModel).
 */
package models;

/**
 * Contains renderer objects that render the coloured patch (GeometryTableIconRenderer) and the remove button
 * (LayerRemoveButtonRenderer) for each layer instance in the table.
 * of contents.
 */
package renderers;

/**
 * Contains various test suites for testing individual parts of the application.
 */
package tester;

/**
 * Contains miscellaneous utilities used throughout the application.
 */
package toolset;