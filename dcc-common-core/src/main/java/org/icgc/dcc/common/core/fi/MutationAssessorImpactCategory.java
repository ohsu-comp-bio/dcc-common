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
package org.icgc.dcc.common.core.fi;

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
public enum MutationAssessorImpactCategory implements ImpactPredictorCategory {

  /**
   * In order of increasing priority.
   */
  LOW("Low"),
  MEDIUM("Medium"),
  HIGH("High");

  private final String id;

  public static MutationAssessorImpactCategory byId(@NonNull String id) {
    for (val value : values()) {
      if (value.getId().equals(id)) {
        return value;
      }
    }

    throw new IllegalArgumentException("Unknown id '" + id + "'  for " + MutationAssessorImpactCategory.class);
  }

  @Override
  public int getPriority() {
    return ordinal();
  }

  @Override
  public ImpactPredictorType getPredictorType() {
    return ImpactPredictorType.MUTATION_ASSESSOR;
  }

  @Override
  public String toString() {
    return id;
  }

}
