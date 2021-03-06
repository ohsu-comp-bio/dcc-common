/*
 * Copyright (c) 2016 The Ontario Institute for Cancer Research. All rights reserved.
 *                                                                                                               
 * This program and the accompanying materials are made available under the terms of the GNU Public License v3.0.
 * You should have received a copy of the GNU General Public License along with                                  
 * this program. If not, see <http://www.gnu.org/licenses/>.                                                     
 *                                                                                                               
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY                           
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES                          
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT                           
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,                                
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED                          
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;                               
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER                              
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN                         
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.icgc.dcc.common.tcga.client;

import static org.icgc.dcc.common.core.util.Formats.formatCount;
import static org.icgc.dcc.common.gdc.client.GDCClient.Query.query;

import java.util.List;
import java.util.stream.Stream;

import org.icgc.dcc.common.gdc.client.GDCClient;
import org.icgc.dcc.common.gdc.reader.GDCReader;
import org.icgc.dcc.common.tcga.core.TCGAMappings;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Stopwatch;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;

import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TCGAClient {

  /**
   * Constants
   */

  // Requires both of these for ICGC
  private static final String CURRENT_API_URL = "https://gdc-api.nci.nih.gov";
  private static final String LEGACY_API_URL = "https://gdc-api.nci.nih.gov/legacy";

  private static final int PAGE_SIZE = 1000;
  private static final List<String> FIELD_NAMES =
      ImmutableList.of(
          "case_id",
          "submitter_id",
          "samples.sample_id",
          "samples.submitter_id",
          "samples.portions.analytes.aliquots.aliquot_id",
          "samples.portions.analytes.aliquots.submitter_id");

  /**
   * State.
   */
  public TCGAMappings getMappings() {
    log.info("Creating UUID <-> barcode mapping...");
    val watch = Stopwatch.createStarted();
    val cases = readCases();

    // Allow for lookup by barcode or UUID value
    log.info("Adding mappings...");
    val mapping = HashBiMap.<String, String> create();
    cases.forEach((caze) -> addMappings(mapping, caze));

    log.info("Finished creating {} mappings in {}", formatCount(mapping), watch);
    return new TCGAMappings(mapping);
  }

  private void addMappings(BiMap<String, String> mapping, ObjectNode caze) {
    // Add donor id mapping
    mapping.put(caze.get("case_id").textValue(), caze.get("submitter_id").textValue());
    for (val sample : caze.path("samples")) {
      // Add specimen id mapping
      mapping.put(sample.get("sample_id").textValue(), sample.get("submitter_id").textValue());

      for (val portion : sample.path("portions")) {
        for (val analyte : portion.path("analytes")) {
          for (val aliquot : analyte.path("aliquots")) {
            // Add sample id mapping
            mapping.put(aliquot.get("aliquot_id").textValue(), aliquot.get("submitter_id").textValue());
          }
        }
      }
    }
  }

  private Stream<ObjectNode> readCases() {
    log.info("Reading current cases...");
    val currentFiles = readCases(CURRENT_API_URL);
    log.info("Reading legacy cases...");
    val legacyFiles = readCases(LEGACY_API_URL);

    return Stream.concat(legacyFiles, currentFiles);
  }

  private Stream<ObjectNode> readCases(String apiUrl) {
    val reader = new GDCReader(new GDCClient(apiUrl)::getCases);
    val query = query().fields(FIELD_NAMES).size(PAGE_SIZE).build();

    return reader.read(query);
  }

}
