/*
 * Copyright (c) 2016 The Ontario Institute for Cancer Research. All rights reserved.
 *                                                                                                               
 * This program and the accompanying materials are made available under the terms of the GNU Public License v3.0.
 * You should have received a copy of the GNU General Public License along with                                  
 * this program. If not, see <http://www.gnu.org/licenses/>.                                                     
 *                                                                                                               
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY                           
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES                          
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT                           
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,                                
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED                          
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;                               
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER                              
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN                         
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.icgc.dcc.common.core.model;

import static lombok.AccessLevel.PRIVATE;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import org.icgc.dcc.common.core.util.IdentifiableSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
@JsonSerialize(using = IdentifiableSerializer.class)
public enum ConsequenceType implements Identifiable {

  /**
   * In order of increasing priority.
   */
  UNKNOWN_CONSEQUENCE("unknown_consequence"),
  CUSTOM("custom"),
  CHROMOSOME("chromosome"),
  INTERGENIC_REGION("intergenic_region"),
  INTRAGENIC_VARIANT("intragenic_variant"),
  GENE_VARIANT("gene_variant"),
  TRANSCRIPT_VARIANT("transcript_variant"),
  INTRON_VARIANT("intron_variant"),
  DOWNSTREAM_GENE_VARIANT("downstream_gene_variant"),
  EXON_VARIANT("exon_variant"),
  _3_PRIME_UTR_VARIANT("3_prime_UTR_variant"),
  STOP_RETAINED_VARIANT("stop_retained_variant"),
  SYNONYMOUS_VARIANT("synonymous_variant"),
  UPSTREAM_GENE_VARIANT("upstream_gene_variant"),
  _5_PRIME_UTR_VARIANT("5_prime_UTR_variant"),
  CONSERVED_INTERGENIC_VARIANT("conserved_intergenic_variant"),
  CONSERVED_INTRON_VARIANT("conserved_intron_variant"),
  MI_RNA("miRNA"),
  REGULATORY_REGION_VARIANT("regulatory_region_variant"),
  INFRAME_INSERTION("inframe_insertion"),
  DISRUPTIVE_INFRAME_INSERTION("disruptive_inframe_insertion"),
  INFRAME_DELETION("inframe_deletion"),
  DISRUPTIVE_INFRAME_DELETION("disruptive_inframe_deletion"),
  NON_CANONICAL_START_CODON("non_canonical_start_codon"),
  _3_PRIME_UTR_TRUNCATION("3_prime_UTR_truncation"),
  _5_PRIME_UTR_TRUNCATION("5_prime_UTR_truncation"),
  CODING_SEQUENCE_VARIANT("coding_sequence_variant"),
  _5_PRIME_UTR_PREMATURE_START_CODON_GAIN_VARIANT("5_prime_UTR_premature_start_codon_gain_variant"),
  RARE_AMINO_ACID_VARIANT("rare_amino_acid_variant"),
  SPLICE_REGION_VARIANT("splice_region_variant"),
  SPLICE_DONOR_VARIANT("splice_donor_variant"),
  SPLICE_ACCEPTOR_VARIANT("splice_acceptor_variant"),
  EXON_LOSS_VARIANT("exon_loss_variant"),
  @Deprecated
  EXON_LOST("exon_lost"),
  STOP_LOST("stop_lost"),
  STOP_GAINED("stop_gained"),
  INITIATOR_CODON_VARIANT("initiator_codon_variant"),
  START_LOST("start_lost"),
  MISSENSE_VARIANT("missense_variant"),
  FRAMESHIFT_VARIANT("frameshift_variant");

  private final String id;

  public static ConsequenceType byId(@NonNull String id) {
    for (val value : values()) {
      if (value.getId().equals(id)) {
        return value;
      }
    }

    throw new IllegalArgumentException("Unknown id '" + id + "'  for " + ConsequenceType.class);
  }

  public int getPriority() {
    return ordinal();
  }

  @Override
  public String toString() {
    return id;
  }

}
