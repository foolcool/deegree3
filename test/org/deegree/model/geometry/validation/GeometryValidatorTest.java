//$HeadURL: svn+ssh://mschneider@svn.wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/
package org.deegree.model.geometry.validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import junit.framework.Assert;

import org.deegree.commons.xml.XMLParsingException;
import org.deegree.commons.xml.stax.XMLStreamReaderWrapper;
import org.deegree.model.crs.exceptions.UnknownCRSException;
import org.deegree.model.geometry.Geometry;
import org.deegree.model.geometry.GeometryFactory;
import org.deegree.model.geometry.GeometryFactoryCreator;
import org.deegree.model.geometry.primitive.Curve;
import org.deegree.model.geometry.primitive.Point;
import org.deegree.model.geometry.primitive.Ring;
import org.deegree.model.geometry.primitive.surfacepatches.PolygonPatch;
import org.deegree.model.gml.GML311GeometryParser;
import org.deegree.model.gml.GML311GeometryParserTest;
import org.junit.Test;

/**
 * Testcases that check the correct determination of topological errors in {@link GeometryValidator}.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 */
public class GeometryValidatorTest {
    
    private static GeometryFactory geomFac = GeometryFactoryCreator.getInstance().getGeometryFactory( "Standard" );

    private static final String BASE_DIR = "testdata/geometries/";

    @Test
    public void validateCurve()
                            throws XMLStreamException, FactoryConfigurationError, IOException, XMLParsingException, UnknownCRSException {
        TestValidationEventHandler eventHandler = new TestValidationEventHandler();
        GeometryValidator validator = new GeometryValidator( eventHandler );
        Geometry geom = parseGeometry( "Curve.gml" );
        Assert.assertTrue( validator.validateGeometry( geom ) );
        Assert.assertTrue( eventHandler.getEvents().isEmpty() );
    }

    @Test
    public void validateInvalidCurve()
                            throws XMLStreamException, FactoryConfigurationError, IOException, XMLParsingException, UnknownCRSException {
        TestValidationEventHandler eventHandler = new TestValidationEventHandler();
        GeometryValidator validator = new GeometryValidator( eventHandler );
        Geometry geom = parseGeometry( "invalid/Curve_discontinuity.gml" );
        Assert.assertFalse( validator.validateGeometry( geom ) );
        Assert.assertEquals( 1, eventHandler.getEvents().size() );
        Assert.assertEquals( ValidationEventType.CURVE_DISCONTINUITY, eventHandler.getEvents().get( 0 ) );
    }

    @Test
    public void validateRing()
                            throws XMLStreamException, FactoryConfigurationError, IOException, XMLParsingException, UnknownCRSException {
        TestValidationEventHandler eventHandler = new TestValidationEventHandler();
        GeometryValidator validator = new GeometryValidator( eventHandler );
        Geometry geom = parseGeometry( "Ring.gml" );
        Assert.assertTrue( validator.validateGeometry( geom ) );
        Assert.assertTrue( eventHandler.getEvents().isEmpty() );
    }

    @Test
    public void validateInvalidRing()
                            throws XMLStreamException, FactoryConfigurationError, IOException, XMLParsingException, UnknownCRSException {
        TestValidationEventHandler eventHandler = new TestValidationEventHandler();
        GeometryValidator validator = new GeometryValidator( eventHandler );
        Geometry geom = parseGeometry( "invalid/Ring_not_closed.gml" );
        Assert.assertFalse( "Geometry must be recognized as invalid.", validator.validateGeometry( geom ) );
        Assert.assertEquals( 1, eventHandler.getEvents().size() );
        Assert.assertEquals( ValidationEventType.RING_NOT_CLOSED, eventHandler.getEvents().get( 0 ) );
    }

    @Test
    public void validateInvalidRing2()
                            throws XMLStreamException, FactoryConfigurationError, IOException, XMLParsingException, UnknownCRSException {
        TestValidationEventHandler eventHandler = new TestValidationEventHandler();
        GeometryValidator validator = new GeometryValidator( eventHandler );
        Geometry geom = parseGeometry( "invalid/Ring_self_intersection.gml" );
        Assert.assertFalse( "Geometry must be recognized as invalid.", validator.validateGeometry( geom ) );
        Assert.assertEquals( 2, eventHandler.getEvents().size() );
        Assert.assertEquals( ValidationEventType.CURVE_SELF_INTERSECTION, eventHandler.getEvents().get( 0 ) );
        Assert.assertEquals( ValidationEventType.RING_SELF_INTERSECTION, eventHandler.getEvents().get( 1 ) );
    }

    @Test
    public void validateInvalidRing3()
                            throws XMLStreamException, FactoryConfigurationError, IOException, XMLParsingException, UnknownCRSException {
        TestValidationEventHandler eventHandler = new TestValidationEventHandler();
        GeometryValidator validator = new GeometryValidator( eventHandler );
        Geometry geom = parseGeometry( "invalid/Ring_not_closed_and_self_intersection.gml" );
        Assert.assertFalse( "Geometry must be recognized as invalid.", validator.validateGeometry( geom ) );
        Assert.assertEquals( 3, eventHandler.getEvents().size() );
        Assert.assertEquals( ValidationEventType.CURVE_SELF_INTERSECTION, eventHandler.getEvents().get( 0 ) );
        Assert.assertEquals( ValidationEventType.RING_SELF_INTERSECTION, eventHandler.getEvents().get( 1 ) );
        Assert.assertEquals( ValidationEventType.RING_NOT_CLOSED, eventHandler.getEvents().get( 2 ) );
    }

    @Test
    public void validatePolygon()
                            throws XMLStreamException, FactoryConfigurationError, IOException, XMLParsingException, UnknownCRSException {
        TestValidationEventHandler eventHandler = new TestValidationEventHandler();
        GeometryValidator validator = new GeometryValidator( eventHandler );
        Geometry geom = parseGeometry( "Polygon.gml" );
        Assert.assertTrue( validator.validateGeometry( geom ) );
        Assert.assertTrue( eventHandler.getEvents().isEmpty() );
    }

    @Test
    public void validateInvalidPolygon1()
                            throws XMLStreamException, FactoryConfigurationError, IOException, XMLParsingException, UnknownCRSException {
        TestValidationEventHandler eventHandler = new TestValidationEventHandler();
        GeometryValidator validator = new GeometryValidator( eventHandler );
        Geometry geom = parseGeometry( "invalid/Polygon_exterior_clockwise.gml" );
        Assert.assertFalse( validator.validateGeometry( geom ) );
        Assert.assertEquals( 1, eventHandler.getEvents().size() );
        Assert.assertEquals( ValidationEventType.SURFACE_EXTERIOR_RING_CW, eventHandler.getEvents().get( 0 ) );
    }
    
    @Test
    public void validateInvalidPolygon2()
                            throws XMLStreamException, FactoryConfigurationError, IOException, XMLParsingException, UnknownCRSException {
        TestValidationEventHandler eventHandler = new TestValidationEventHandler();
        GeometryValidator validator = new GeometryValidator( eventHandler );
        Geometry geom = parseGeometry( "invalid/Polygon_interiors_counterclockwise.gml" );
        Assert.assertFalse( validator.validateGeometry( geom ) );
        Assert.assertEquals( 2, eventHandler.getEvents().size() );
        Assert.assertEquals( ValidationEventType.SURFACE_INTERIOR_RING_CCW, eventHandler.getEvents().get( 0 ) );
        Assert.assertEquals( ValidationEventType.SURFACE_INTERIOR_RING_CCW, eventHandler.getEvents().get( 1 ) );
    }    
    
    @Test
    public void validateInvalidPolygon3()
                            throws XMLStreamException, FactoryConfigurationError, IOException, XMLParsingException, UnknownCRSException {
        TestValidationEventHandler eventHandler = new TestValidationEventHandler();
        GeometryValidator validator = new GeometryValidator( eventHandler );
        Geometry geom = parseGeometry( "invalid/Polygon_exterior_not_closed.gml" );
        Assert.assertFalse( validator.validateGeometry( geom ) );
        Assert.assertEquals( 1, eventHandler.getEvents().size() );
        Assert.assertEquals( ValidationEventType.RING_NOT_CLOSED, eventHandler.getEvents().get( 0 ) );
    }

    @Test
    public void validateInvalidPolygon4()
                            throws XMLStreamException, FactoryConfigurationError, IOException, XMLParsingException, UnknownCRSException {
        TestValidationEventHandler eventHandler = new TestValidationEventHandler();
        GeometryValidator validator = new GeometryValidator( eventHandler );
        Geometry geom = parseGeometry( "invalid/Polygon_interior_outside_exterior.gml" );
        Assert.assertFalse( validator.validateGeometry( geom ) );
        Assert.assertEquals( 1, eventHandler.getEvents().size() );
        Assert.assertEquals( ValidationEventType.SURFACE_INTERIOR_RING_OUTSIDE_EXTERIOR,
                             eventHandler.getEvents().get( 0 ) );
    }

    @Test
    public void validateInvalidPolygon5()
                            throws XMLStreamException, FactoryConfigurationError, IOException, XMLParsingException, UnknownCRSException {
        TestValidationEventHandler eventHandler = new TestValidationEventHandler();
        GeometryValidator validator = new GeometryValidator( eventHandler );
        Geometry geom = parseGeometry( "invalid/Polygon_interiors_touch.gml" );
        Assert.assertFalse( validator.validateGeometry( geom ) );
        Assert.assertEquals( ValidationEventType.SURFACE_INTERIOR_RINGS_INTERSECT,
                             eventHandler.getEvents().get( 0 ) );        
    }

    @Test
    public void validateInvalidPolygon6()
                            throws XMLStreamException, FactoryConfigurationError, IOException, XMLParsingException, UnknownCRSException {
        TestValidationEventHandler eventHandler = new TestValidationEventHandler();
        GeometryValidator validator = new GeometryValidator( eventHandler );
        Geometry geom = parseGeometry( "invalid/Polygon_interiors_intersect.gml" );
        Assert.assertFalse( validator.validateGeometry( geom ) );
        Assert.assertEquals( ValidationEventType.SURFACE_INTERIOR_RINGS_INTERSECT,
                             eventHandler.getEvents().get( 0 ) );        
    }    

    @Test
    public void validateInvalidPolygon7()
                            throws XMLStreamException, FactoryConfigurationError, IOException, XMLParsingException, UnknownCRSException {
        TestValidationEventHandler eventHandler = new TestValidationEventHandler();
        GeometryValidator validator = new GeometryValidator( eventHandler );
        Geometry geom = parseGeometry( "invalid/Polygon_interior_outside_exterior.gml" );
        Assert.assertFalse( validator.validateGeometry( geom ) );
        Assert.assertEquals( ValidationEventType.SURFACE_INTERIOR_RING_OUTSIDE_EXTERIOR,
                             eventHandler.getEvents().get( 0 ) );        
    }

    @Test
    public void validateInvalidPolygon8()
                            throws XMLStreamException, FactoryConfigurationError, IOException, XMLParsingException, UnknownCRSException {
        TestValidationEventHandler eventHandler = new TestValidationEventHandler();
        GeometryValidator validator = new GeometryValidator( eventHandler );
        Geometry geom = parseGeometry( "invalid/Polygon_interior_touches_exterior.gml" );
        Assert.assertFalse( validator.validateGeometry( geom ) );
        Assert.assertEquals( ValidationEventType.SURFACE_INTERIOR_RING_INTERSECTS_EXTERIOR,
                             eventHandler.getEvents().get( 0 ) );        
    }

    @Test
    public void validateInvalidPolygon9()
                            throws XMLStreamException, FactoryConfigurationError, IOException, XMLParsingException, UnknownCRSException {
        TestValidationEventHandler eventHandler = new TestValidationEventHandler();
        GeometryValidator validator = new GeometryValidator( eventHandler );
        Geometry geom = parseGeometry( "invalid/Polygon_interior_intersects_exterior.gml" );
        Assert.assertFalse( validator.validateGeometry( geom ) );
        Assert.assertEquals( ValidationEventType.SURFACE_INTERIOR_RING_INTERSECTS_EXTERIOR,
                             eventHandler.getEvents().get( 0 ) );        
    }    
    
    private Geometry parseGeometry( String fileName )
                            throws XMLStreamException, FactoryConfigurationError, IOException, XMLParsingException, UnknownCRSException {
        XMLStreamReaderWrapper xmlReader = new XMLStreamReaderWrapper(
                                                                       GML311GeometryParserTest.class.getResource( BASE_DIR
                                                                                                                   + fileName ) );
        xmlReader.nextTag();
        return new GML311GeometryParser( geomFac, xmlReader ).parseGeometry( null );
    }
}

class TestValidationEventHandler implements GeometryValidationEventHandler {

    private List<ValidationEventType> events = new ArrayList<ValidationEventType>();

    @Override
    public boolean curveDiscontinuity( Curve curve, int segmentIdx, List<Object> affectedGeometryParticles) {
        events.add( ValidationEventType.CURVE_DISCONTINUITY );
        printAffectedGeometryParticles(affectedGeometryParticles);
        return false;
    }

    @Override
    public boolean curvePointDuplication( Curve curve, Point point, List<Object> affectedGeometryParticles ) {
        events.add( ValidationEventType.CURVE_DUPLICATE_POINT );
        printAffectedGeometryParticles(affectedGeometryParticles);
        return false;
    }

    @Override
    public boolean curveSelfIntersection( Curve curve, Point location, List<Object> affectedGeometryParticles ) {
        events.add( ValidationEventType.CURVE_SELF_INTERSECTION );
        printAffectedGeometryParticles(affectedGeometryParticles);
        return false;
    }

    @Override
    public boolean exteriorRingCW( PolygonPatch patch, List<Object> affectedGeometryParticles ) {
        events.add( ValidationEventType.SURFACE_EXTERIOR_RING_CW );
        printAffectedGeometryParticles(affectedGeometryParticles);
        return false;
    }

    @Override
    public boolean interiorRingCCW( PolygonPatch patch, List<Object> affectedGeometryParticles ) {
        events.add( ValidationEventType.SURFACE_INTERIOR_RING_CCW );
        printAffectedGeometryParticles(affectedGeometryParticles);
        return false;
    }

    @Override
    public boolean interiorRingIntersectsExterior( PolygonPatch patch, int ringIdx, List<Object> affectedGeometryParticles ) {
        events.add( ValidationEventType.SURFACE_INTERIOR_RING_INTERSECTS_EXTERIOR );
        printAffectedGeometryParticles(affectedGeometryParticles);
        return false;
    }

    @Override
    public boolean interiorRingOutsideExterior( PolygonPatch patch, int ringIdx, List<Object> affectedGeometryParticles ) {
        events.add( ValidationEventType.SURFACE_INTERIOR_RING_OUTSIDE_EXTERIOR );
        printAffectedGeometryParticles(affectedGeometryParticles);
        return false;
    }

    @Override
    public boolean interiorRingTouchesExterior( PolygonPatch patch, int ringIdx, List<Object> affectedGeometryParticles ) {
        events.add( ValidationEventType.SURFACE_INTERIOR_RING_TOUCHES_EXTERIOR );
        printAffectedGeometryParticles(affectedGeometryParticles);
        return false;
    }

    @Override
    public boolean interiorRingsIntersect( PolygonPatch patch, int ring1Idx, int ring2Idx, List<Object> affectedGeometryParticles ) {
        events.add( ValidationEventType.SURFACE_INTERIOR_RINGS_INTERSECT );
        printAffectedGeometryParticles(affectedGeometryParticles);
        return false;
    }

    @Override
    public boolean interiorRingsTouch( PolygonPatch patch, int ring1Idx, int ring2Idx, List<Object> affectedGeometryParticles ) {
        events.add( ValidationEventType.SURFACE_INTERIOR_RINGS_TOUCH );
        printAffectedGeometryParticles(affectedGeometryParticles);
        return false;
    }

    @Override
    public boolean interiorRingsWithin( PolygonPatch patch, int ring1Idx, int ring2Idx, List<Object> affectedGeometryParticles ) {
        events.add( ValidationEventType.SURFACE_INTERIOR_RINGS_NESTED );
        printAffectedGeometryParticles(affectedGeometryParticles);
        return false;
    }

    @Override
    public boolean ringNotClosed( Ring ring, List<Object> affectedGeometryParticles ) {
        events.add( ValidationEventType.RING_NOT_CLOSED );
        printAffectedGeometryParticles(affectedGeometryParticles);
        return false;
    }

    @Override
    public boolean ringSelfIntersection( Ring ring, Point location, List<Object> affectedGeometryParticles ) {
        events.add( ValidationEventType.RING_SELF_INTERSECTION );
        printAffectedGeometryParticles(affectedGeometryParticles);
        return false;
    }

    List<ValidationEventType> getEvents() {
        return events;
    }
    
    private void printAffectedGeometryParticles(List<Object> affectedGeometryParticles) {
        String indent = "";
        for ( Object object : affectedGeometryParticles ) {
            System.out.println (indent + "-" + object);
            indent += "  ";
        }
    }
}
