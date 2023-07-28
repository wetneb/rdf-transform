/*
 *  Class SaveRDFTransformCommand
 *
 *  Saves the current RDF Transform as the last entry in the OpenRefine
 *  project history.
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

package org.openrefine.rdf.command;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrefine.model.Project;
import org.openrefine.rdf.RDFTransform;
import org.openrefine.rdf.model.Util;
import org.openrefine.rdf.model.operation.SaveRDFTransformOperation;
import org.openrefine.util.ParsingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

public class SaveRDFTransformCommand extends RDFTransformCommand {
    private final static Logger logger = LoggerFactory.getLogger("RDFT:SaveRDFTransCmd");

    public SaveRDFTransformCommand() {
        super();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ( Util.isDebugMode() ) SaveRDFTransformCommand.logger.info("DEBUG: Reconstructing Transform for Save...");
        if ( ! this.hasValidCSRFToken(request) ) {
            if ( Util.isDebugMode() ) SaveRDFTransformCommand.logger.info("  No CSRF Token.");
            SaveRDFTransformCommand.respondCSRFError(response);
            return;
        }

        // Get the project...
        Project theProject = this.getProject(request);

        // Get the RDF Transform...
        String strTransform = request.getParameter(RDFTransform.KEY);
        if (strTransform == null) {
            if ( Util.isDebugMode() ) SaveRDFTransformCommand.logger.info("  No Transform JSON.");
            SaveRDFTransformCommand.respondJSON(response, CodeResponse.error);
            return;
        }
        JsonNode jnodeTransform = ParsingUtilities.evaluateJsonStringToObjectNode(strTransform);
        if ( jnodeTransform == null || jnodeTransform.isNull() || jnodeTransform.isEmpty() ) {
            if ( Util.isDebugMode() ) SaveRDFTransformCommand.logger.info("  No Transform.");
            SaveRDFTransformCommand.respondJSON(response, CodeResponse.error);
            return;
        }
        RDFTransform theTransform = RDFTransform.reconstruct(theProject, jnodeTransform);
        if ( Util.isDebugMode() ) SaveRDFTransformCommand.logger.info("  Transform reconstructed.");

        // Process the "save" operations...
        SaveRDFTransformOperation opSave = new SaveRDFTransformOperation(theTransform);
        theProject.getHistory().addEntry(opSave);
        respondJSON(response, 200, CodeResponse.ok);
    }
}
