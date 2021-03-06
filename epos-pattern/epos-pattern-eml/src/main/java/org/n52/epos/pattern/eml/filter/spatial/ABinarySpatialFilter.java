/**
 * Copyright (C) 2013
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.epos.pattern.eml.filter.spatial;


import net.opengis.fes.x20.BinarySpatialOpType;

import org.apache.xmlbeans.XmlObject;
import org.n52.epos.event.MapEposEvent;
import org.n52.epos.pattern.eml.filter.expression.LiteralExpression;
import org.n52.epos.pattern.functions.MethodNames;
import org.n52.oxf.conversion.gml32.xmlbeans.jts.GMLGeometryFactory;
import org.n52.oxf.xmlbeans.tools.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;


/**
 * 
 * @author Matthes Rieke <m.rieke@uni-muenster.de>
 *
 */
public abstract class ABinarySpatialFilter extends ASpatialFilter {

	/**
	 * the operator of this instance 
	 */
	protected BinarySpatialOpType bsOperator;

	/**
	 * the global logger
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(ABinarySpatialFilter.class);

	/**
	 * 
	 * Constructor
	 *
	 * @param bsOp FES binary spatial operator
	 */
	public ABinarySpatialFilter(BinarySpatialOpType bsOp) {
		this.bsOperator = bsOp;
	}

	/**
	 * @param methodName the java method name of the spatial filter
	 * @return the expression string
	 */
	protected String createExpressionForBinaryFilter(String methodName) {
		Geometry geom = null;

		XmlObject[] literals = this.bsOperator.selectChildren(LiteralExpression.FES_2_0_LITERAL_NAME);

		if (literals.length > 1) {
			logger.warn("Multiple fes:Literal in expression. using the first.");
		}
		if (literals.length >= 1) {

			XmlObject[] children = XmlUtil.selectPath("./*", literals[0]);

			if (children.length > 1) {
				logger.warn("Multiple children in fes:Literal. using the first.");
			}
			if (children.length >= 1) {
				try {
					geom = GMLGeometryFactory.parseGeometry(children[0]);
				} catch (ParseException e) {
					logger.warn(e.getMessage(), e);
				}
			}

		}

		if (geom == null) {
			logger.warn("Only gml:Envelope supported at the current developement state.");
			return null;
		}
		
		//TODO reihenfolge
		
		StringBuilder sb = new StringBuilder();

		sb.append(MethodNames.SPATIAL_METHODS_PREFIX);
		sb.append(methodName+ "(");
		//create WKT from corners
		//TODO actually resolve the property name from the ValueReference?!
		sb.append(MapEposEvent.GEOMETRY_KEY );
		sb.append(", ");
		sb.append(MethodNames.SPATIAL_METHODS_PREFIX);
		sb.append("fromWKT(\""+ geom.toText() +"\"))");

		return sb.toString();
	}

}
