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

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.toArray;
import static lombok.AccessLevel.PRIVATE;
import static org.icgc.dcc.common.core.util.Joiners.COLON;
import static org.icgc.dcc.common.core.util.Joiners.PATH;
import static org.icgc.dcc.common.core.util.Optionals.ABSENT_STRING;

import java.net.URI;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

import com.google.common.base.Optional;

/**
 * Utility methods and constants for {@link URI}s.
 * <p>
 * TODO: change to decorator? + write test class
 */
@NoArgsConstructor(access = PRIVATE)
public final class URIs {

  public static final String LOCAL_ROOT = Protocol.FILE.getId() + Separators.PATH;
  public static final String HDFS_ROOT = Protocol.HDFS.getId() + Separators.PATH;

  private static final int USERNAME_OFFSET = 0;
  private static final int PASSWORD_OFFSET = USERNAME_OFFSET + 1;

  @SneakyThrows
  public static URI getUri(@NonNull Protocol protocol, @NonNull String host, int port, Optional<String> optionalPath) {
    return new URI(getUriString(protocol, host, port, optionalPath));
  }

  public static String getUriString(@NonNull Protocol protocol, @NonNull String host, final int port,
      Optional<String> optionalPath) {
    String base = protocol.getId() + COLON.join(host, port);

    return optionalPath.isPresent() ? PATH.join(base, optionalPath.get()) : base;
  }

  @SneakyThrows
  public static URI getUri(@NonNull String value) {
    return new URI(value);
  }

  @SneakyThrows
  public static URI getQualifiedUri(String fsUri) {
    val schemeSeparator = "://";
    val defaultScheme = "http";
    val defaultPrefix = defaultScheme + schemeSeparator;

    return new URI(fsUri.contains(schemeSeparator) ? fsUri : defaultPrefix + fsUri);
  }

  public static Optional<String> getHost(@NonNull URI uri) {
    return fromNullable(uri.getHost());
  }

  public static Optional<Integer> getPort(@NonNull URI uri) {
    return fromNullable(uri.getPort());
  }

  public static Optional<String> getUsername(@NonNull URI uri) {
    return uri.getUserInfo() != null ?
        Optional.of(getCredentials(uri.getUserInfo()).getKey()) :
        ABSENT_STRING;
  }

  public static Optional<String> getPassword(@NonNull URI uri) {
    return uri.getUserInfo() != null ?
        Optional.of(getCredentials(uri.getUserInfo()).getValue()) :
        ABSENT_STRING;
  }

  private static Entry<String, String> getCredentials(@NonNull String userInfo) {
    String[] credentials = toArray(
        Splitters.CREDENTIALS.split(firstNonNull(userInfo, Separators.CREDENTIALS)),
        String.class);
    checkState(credentials.length == PASSWORD_OFFSET + 1, // TODO: case where only the username is provided possible?
        "Credentials are expected to have 2 components, a username and a password");

    return new SimpleEntry<String, String>(
        credentials[USERNAME_OFFSET],
        credentials[PASSWORD_OFFSET]);
  }

  @SneakyThrows
  public static URI fromUrl(@NonNull URL url) {
    return url.toURI();
  }

}
