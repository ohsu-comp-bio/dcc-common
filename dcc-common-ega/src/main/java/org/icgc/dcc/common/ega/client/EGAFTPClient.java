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
package org.icgc.dcc.common.ega.client;

import static com.google.common.io.Resources.readLines;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;
import static org.icgc.dcc.common.core.util.function.Predicates.not;
import static org.icgc.dcc.common.core.util.stream.Collectors.toImmutableList;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import lombok.experimental.Accessors;

/**
 * FTP client to access EGA "boxes"
 */
public class EGAFTPClient {

  /**
   * Constants.
   */
  public static final String DEFAULT_USERNAME = "ega-box-138";
  public static final String DEFAULT_HOST = "ftp-private.ebi.ac.uk";

  private static final String METADATA_DIR = "ICGC_metadata";
  private static final Pattern DATASET_ID_PATTERN = Pattern.compile("(EGAD\\d+)");

  // @formatter:off
  private static final Pattern LISTING_PATTERN = Pattern.compile(
          "(\\d+)        # File size           \n" +
          "\\s+          # Whitespace          \n" +
          "(\\w{3}       # Month (3 letters)   \n" +
          "\\s+          # Whitespace          \n" +
          "\\d{1,2}      # Day (1 or 2 digits) \n" +
          "\\s+          # Whitespace          \n" +
          "[\\d:]{4,5})  # Time or year        \n" +
          "\\s+          # Whitespace          \n" +
          "(.*)          # Filename            \n" +
          "$             # End of line",
      Pattern.MULTILINE | Pattern.COMMENTS);
  // @formatter:on

  /**
   * Configuration.
   */
  private final String url;

  /**
   * State.
   */
  @Accessors(fluent = true)
  @Getter(lazy = true, value = PRIVATE)
  private final List<String> datasetIds = getListing(METADATA_DIR).stream()
      .map(this::parseDatasetId)
      .collect(toList());

  public EGAFTPClient() {
    this(System.getProperty("ega.ftp.username", DEFAULT_USERNAME), System.getProperty("ega.ftp.password"));
  }

  public EGAFTPClient(@NonNull String user, @NonNull String password) {
    this(DEFAULT_HOST, user, password);
  }

  public EGAFTPClient(@NonNull String host, @NonNull String user, @NonNull String password) {
    this.url = String.format("ftp://%s:%s@%s", user, password, host);
  }

  public List<String> getDatasetIds() {
    return datasetIds();
  }

  @SneakyThrows
  public List<Item> getListing() {
    val dirUrl = getFileURL(METADATA_DIR);
    return readLines(dirUrl, UTF_8).stream()
        .map(this::parseItem)
        .collect(toImmutableList());
  }

  public boolean hasDatasetId(@NonNull String datasetId) {
    return datasetIds().contains(datasetId);
  }

  public URL getArchiveURL(@NonNull String datasetId) {
    return getFileURL(METADATA_DIR + "/" + datasetId + ".tar.gz");
  }

  private String parseDatasetId(String fileName) {
    val matcher = DATASET_ID_PATTERN.matcher(fileName);
    return matcher.find() ? matcher.group(1) : null;
  }

  @SneakyThrows
  private List<String> getListing(String dir) {
    val dirUrl = getDirURL(dir);
    return readLines(dirUrl, UTF_8).stream()
        .filter(not(this::isRelativeDir))
        .collect(toImmutableList());
  }

  private URL getDirURL(String path) {
    return getFileURL(path + ";type=d");
  }

  @SneakyThrows
  private URL getFileURL(String path) {
    return new URL(url + "/" + path);
  }

  private boolean isRelativeDir(String line) {
    return line.equals(".") || line.equals("..");
  }

  private Item parseItem(String line) {
    val m = LISTING_PATTERN.matcher(line);
    m.find();
    return new Item(Long.parseLong(m.group(1)), parseDate(m.group(2)), m.group(3));
  }

  @SneakyThrows
  private static Date parseDate(String date) {
    val dateFormat = new SimpleDateFormat("MMM dd HH:mm yyyy");
    dateFormat.setTimeZone(TimeZone.getTimeZone("CET"));

    val fullDate = date + " " + Calendar.getInstance().get(Calendar.YEAR);
    return dateFormat.parse(fullDate);
  }

  @Value
  public static class Item {

    long size;
    Date updated;
    String fileName;

  }

}
