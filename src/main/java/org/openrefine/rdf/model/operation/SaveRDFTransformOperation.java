/*
 *  Class SaveRDFTransformOperation
 *
 *  The RDF Transform Save Operation used to save the current RDF Transform as
 *  an OpenRefine history entry.
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

package org.openrefine.rdf.model.operation;

import java.util.HashMap;
import java.util.Map;

import org.openrefine.history.GridPreservation;
import org.openrefine.model.Grid;
import org.openrefine.model.Project;
import org.openrefine.model.changes.ChangeContext;
import org.openrefine.operations.ChangeResult;
import org.openrefine.operations.Operation;
import org.openrefine.operations.exceptions.OperationException;
import org.openrefine.overlay.OverlayModel;
import org.openrefine.rdf.RDFTransform;
import org.openrefine.rdf.model.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class SaveRDFTransformOperation implements Operation {
    private final static Logger logger = LoggerFactory.getLogger("RDFT:SaveRDFTransOp");

    /*
        Class Variables
    */
    //@JsonProperty("description")
    @JsonIgnore
    private final static String strSaveRDFTransform = "Save RDF Transform";

    /*
        Instance Variables
    */
    //@JsonProperty(RDFTransform.KEY)
    @JsonIgnore
    private RDFTransform theTransform;

    public SaveRDFTransformOperation() {
        if ( Util.isVerbose(3) || Util.isDebugMode() ) SaveRDFTransformOperation.logger.info("Created empty Save Op.");
    };

    public SaveRDFTransformOperation(RDFTransform theTransform) {
        this.theTransform = theTransform;
        if ( Util.isVerbose(3) || Util.isDebugMode() ) SaveRDFTransformOperation.logger.info("Created Save Op with transform.");
    }

    @Override
    public String getDescription() {
        return SaveRDFTransformOperation.strSaveRDFTransform;
    }

    @JsonProperty(RDFTransform.KEY)
    public RDFTransform getTransform() {
        return this.theTransform;
    }

    @JsonProperty(RDFTransform.KEY)
    public void setTransform(JsonNode jnodeTransform)
            throws Exception {
        if ( Util.isVerbose(3) || Util.isDebugMode() ) SaveRDFTransformOperation.logger.info("Reconstructing from JSON...");
        if ( jnodeTransform == null || jnodeTransform.isNull() || jnodeTransform.isEmpty() ) {
            SaveRDFTransformOperation.logger.info("  No Transform.");
            return;
        }
        this.theTransform = RDFTransform.reconstruct(jnodeTransform);
        if ( Util.isDebugMode() ) SaveRDFTransformOperation.logger.info("...reconstructed from JSON.");
    }

    @Override
    public ChangeResult apply(Grid projectState, ChangeContext context) throws OperationException {
        Map<String, OverlayModel> newOverlayModels = new HashMap<>(projectState.getOverlayModels());
        Grid newGrid = projectState.withOverlayModels(newOverlayModels);
        return new ChangeResult(newGrid, GridPreservation.PRESERVES_RECORDS, null);
    }

    static public Operation reconstruct(Project theProject, JsonNode jnodeElement)
            throws Exception {
        if ( Util.isVerbose(3) || Util.isDebugMode() ) SaveRDFTransformOperation.logger.info("Reconstructing from Save Operation...");
        JsonNode jnodeTransform = jnodeElement.get(RDFTransform.KEY);
        if (jnodeTransform == null || jnodeTransform.isNull() || jnodeTransform.isEmpty() ) {
            if ( Util.isDebugMode() ) SaveRDFTransformOperation.logger.info("  No Transform.");
            return null;
        }
        RDFTransform theTransform = RDFTransform.reconstruct(theProject, jnodeTransform);
        return new SaveRDFTransformOperation(theTransform);
    }

}
