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
package org.icgc.dcc.common.core.meta;

import static com.google.common.net.HttpHeaders.ACCEPT;

import java.io.InputStream;
import java.net.URL;

import lombok.SneakyThrows;
import lombok.val;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;

public interface Resolver {

  /**
   * Abstraction that resolves the content of the most current dictionary.
   */
  public interface DictionaryResolver extends Supplier<ObjectNode>, Function<Optional<String>, ObjectNode> {}

  /**
   * Abstraction that resolves the content of the code lists.
   */
  public interface CodeListsResolver extends Supplier<ArrayNode> {}

  interface SubmissionSystemResolver extends Resolver {

    String DEFAULT_SCHEME = "http://";
    String DEFAULT_HOST = "localhost";
    int DEFAULT_PORT = 5380;
    String DEFAULT_API_PATH = "/ws";
    String DEFAULT_API_URL = DEFAULT_SCHEME + DEFAULT_HOST + ":" + DEFAULT_PORT + DEFAULT_API_PATH;

    String getSubmissionSystemUrl(Optional<String> qualifier);

    interface SubmissionSystemDictionaryResolver extends DictionaryResolver, SubmissionSystemResolver {

      String PATH_SPECIFIC = "/dictionaries";
      String PATH_CURRENT = "/nextRelease/dictionary";
      String DEFAULT_DICTIONARY_URL = DEFAULT_API_URL + PATH_CURRENT;

    }

    interface SubmissionSystemCodeListsResolver extends CodeListsResolver, SubmissionSystemResolver {

      String PATH = "/codeLists";
      String DEFAULT_CODELISTS_URL = DEFAULT_API_URL + PATH;

    }

  }

  static class Resolvers {

    @SneakyThrows
    static InputStream getContent(String url) {
      val connection = new URL(url).openConnection();
      connection.setRequestProperty(ACCEPT, "application/json");

      return (InputStream) connection.getContent();
    }

  }

}
