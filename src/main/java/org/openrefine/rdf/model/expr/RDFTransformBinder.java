/*
 *  Class RDFTransformBinder
 *
 *  The RDF Transform Expression Binder used to add a baseIRI binding
 *  property.
 *
 *  Copyright 2022 Keven L. Ates
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.openrefine.rdf.model.expr;

import java.util.Map;
import java.util.Properties;

import org.openrefine.expr.Binder;
import org.openrefine.model.Cell;
import org.openrefine.model.Record;
import org.openrefine.model.Row;
import org.openrefine.overlay.OverlayModel;
import org.openrefine.rdf.RDFTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Class RDFBinder
 *
 *   This class is registered by the "controller.js" in this extension.
 *   The purpose of registering this "binder" is to push an instance of this class onto the
 *   HashSet managed by the ExpressionUtils class.
 *
 *   This "binder" is used by the ExpressionUtils createBindings() method to create and add
 *   generic "bindings" properties.  It calls this "binder"'s initializeBindings() method to
 *   add a "baseIRI" binding to the "bindings" properties.
 *
 *   The ExpressionUtils bind() method is used to bind a specific row (Row), row index (int),
 *   column name (String), and cell (Cell) to the "bindings".  It calls this "binder"'s bind()
 *   method to perform any additional work concerning the added "baseIRI" binding.
 */
public class RDFTransformBinder implements Binder {
    private final static Logger logger = LoggerFactory.getLogger("RDFT:RDFBinder");

    @Override
    public void initializeBindings(Properties theBindings) {
    }

    @Override
    public void bind(Properties bindings, Row row, long rowIndex, Record record, String columnName, Cell cell, Map<String, OverlayModel> overlayModels, long projectId) {
        //
        // Adds the baseIRI variable to the context, from the RDF transform associated with the project

        // Get the current baseIRI...
        RDFTransform theTransform = (RDFTransform) overlayModels.get(RDFTransform.EXTENSION);
        if (theTransform == null) {
            // Create a new RDFTransform for the project...
            theTransform = new RDFTransform(projectId);
        }
        
        String strCurrentBaseIRI = theTransform.getBaseIRIAsString();
        bindings.put("baseIRI", strCurrentBaseIRI);
    }
}
