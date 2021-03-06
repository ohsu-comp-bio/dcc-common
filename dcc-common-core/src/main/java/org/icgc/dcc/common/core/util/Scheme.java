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
package org.icgc.dcc.common.core.util;

import static lombok.AccessLevel.PRIVATE;
import static org.icgc.dcc.common.core.util.Strings2.isLowerCase;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.icgc.dcc.common.core.model.Identifiable;

@NoArgsConstructor(access = PRIVATE)
public enum Scheme implements Identifiable {

  FILE,
  CLASSPATH,
  HTTP,
  HTTPS,
  HDFS,
  MONGODB,
  ES,
  S3;

  @Override
  public String getId() {
    return name().toLowerCase();
  }

  public boolean isFile() {
    return this == FILE;
  }

  public boolean isHdfs() {
    return this == HDFS;
  }

  public static Scheme from(@NonNull String scheme) {
    return valueOf(scheme.toUpperCase());
  }

  public static boolean isFile(@NonNull String scheme) {
    return is(scheme, FILE);
  }

  public static boolean isHdfs(@NonNull String scheme) {
    return is(scheme, HDFS);
  }

  public static boolean isHttp(@NonNull String scheme) {
    return is(scheme, HTTP);
  }

  public static boolean isHttps(@NonNull String scheme) {
    return is(scheme, HTTPS);
  }

  public static boolean isClasspath(@NonNull String scheme) {
    return is(scheme, CLASSPATH);
  }

  private static boolean is(String schemeString, Scheme scheme) {
    return isLowerCase(schemeString) && from(schemeString) == scheme;
  }

}
