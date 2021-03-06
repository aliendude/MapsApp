/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2015-08-03 17:34:38 UTC)
 * on 2015-11-08 at 19:22:38 UTC 
 * Modify at your own risk.
 */

package com.example.pedro.myapplication.backend1.mapmarkers;

/**
 * Service definition for Mapmarkers (v1).
 *
 * <p>
 * This is an API
 * </p>
 *
 * <p>
 * For more information about this service, see the
 * <a href="" target="_blank">API Documentation</a>
 * </p>
 *
 * <p>
 * This service uses {@link MapmarkersRequestInitializer} to initialize global parameters via its
 * {@link Builder}.
 * </p>
 *
 * @since 1.3
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public class Mapmarkers extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient {

  // Note: Leave this static initializer at the top of the file.
  static {
    com.google.api.client.util.Preconditions.checkState(
        com.google.api.client.googleapis.GoogleUtils.MAJOR_VERSION == 1 &&
        com.google.api.client.googleapis.GoogleUtils.MINOR_VERSION >= 15,
        "You are currently running with version %s of google-api-client. " +
        "You need at least version 1.15 of google-api-client to run version " +
        "1.20.0 of the mapmarkers library.", com.google.api.client.googleapis.GoogleUtils.VERSION);
  }

  /**
   * The default encoded root URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_ROOT_URL = "https://myApplicationId.appspot.com/_ah/api/";

  /**
   * The default encoded service path of the service. This is determined when the library is
   * generated and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_SERVICE_PATH = "mapmarkers/v1/";

  /**
   * The default encoded base URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   */
  public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

  /**
   * Constructor.
   *
   * <p>
   * Use {@link Builder} if you need to specify any of the optional parameters.
   * </p>
   *
   * @param transport HTTP transport, which should normally be:
   *        <ul>
   *        <li>Google App Engine:
   *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
   *        <li>Android: {@code newCompatibleTransport} from
   *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
   *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
   *        </li>
   *        </ul>
   * @param jsonFactory JSON factory, which may be:
   *        <ul>
   *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
   *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
   *        <li>Android Honeycomb or higher:
   *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
   *        </ul>
   * @param httpRequestInitializer HTTP request initializer or {@code null} for none
   * @since 1.7
   */
  public Mapmarkers(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
      com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
    this(new Builder(transport, jsonFactory, httpRequestInitializer));
  }

  /**
   * @param builder builder
   */
  Mapmarkers(Builder builder) {
    super(builder);
  }

  @Override
  protected void initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest<?> httpClientRequest) throws java.io.IOException {
    super.initialize(httpClientRequest);
  }

  /**
   * Create a request for the method "addMarker".
   *
   * This request holds the parameters needed by the mapmarkers server.  After setting any optional
   * parameters, call the {@link AddMarker#execute()} method to invoke the remote operation.
   *
   * @param content the {@link com.example.pedro.myapplication.backend1.mapmarkers.model.MapMarker}
   * @return the request
   */
  public AddMarker addMarker(com.example.pedro.myapplication.backend1.mapmarkers.model.MapMarker content) throws java.io.IOException {
    AddMarker result = new AddMarker(content);
    initialize(result);
    return result;
  }

  public class AddMarker extends MapmarkersRequest<Void> {

    private static final String REST_PATH = "addMarker";

    /**
     * Create a request for the method "addMarker".
     *
     * This request holds the parameters needed by the the mapmarkers server.  After setting any
     * optional parameters, call the {@link AddMarker#execute()} method to invoke the remote
     * operation. <p> {@link
     * AddMarker#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param content the {@link com.example.pedro.myapplication.backend1.mapmarkers.model.MapMarker}
     * @since 1.13
     */
    protected AddMarker(com.example.pedro.myapplication.backend1.mapmarkers.model.MapMarker content) {
      super(Mapmarkers.this, "POST", REST_PATH, content, Void.class);
    }

    @Override
    public AddMarker setAlt(java.lang.String alt) {
      return (AddMarker) super.setAlt(alt);
    }

    @Override
    public AddMarker setFields(java.lang.String fields) {
      return (AddMarker) super.setFields(fields);
    }

    @Override
    public AddMarker setKey(java.lang.String key) {
      return (AddMarker) super.setKey(key);
    }

    @Override
    public AddMarker setOauthToken(java.lang.String oauthToken) {
      return (AddMarker) super.setOauthToken(oauthToken);
    }

    @Override
    public AddMarker setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (AddMarker) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public AddMarker setQuotaUser(java.lang.String quotaUser) {
      return (AddMarker) super.setQuotaUser(quotaUser);
    }

    @Override
    public AddMarker setUserIp(java.lang.String userIp) {
      return (AddMarker) super.setUserIp(userIp);
    }

    @Override
    public AddMarker set(String parameterName, Object value) {
      return (AddMarker) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "getMapMarkers".
   *
   * This request holds the parameters needed by the mapmarkers server.  After setting any optional
   * parameters, call the {@link GetMapMarkers#execute()} method to invoke the remote operation.
   *
   * @return the request
   */
  public GetMapMarkers getMapMarkers() throws java.io.IOException {
    GetMapMarkers result = new GetMapMarkers();
    initialize(result);
    return result;
  }

  public class GetMapMarkers extends MapmarkersRequest<com.example.pedro.myapplication.backend1.mapmarkers.model.MapMarkerCollection> {

    private static final String REST_PATH = "mapmarkercollection";

    /**
     * Create a request for the method "getMapMarkers".
     *
     * This request holds the parameters needed by the the mapmarkers server.  After setting any
     * optional parameters, call the {@link GetMapMarkers#execute()} method to invoke the remote
     * operation. <p> {@link GetMapMarkers#initialize(com.google.api.client.googleapis.services.Abstra
     * ctGoogleClientRequest)} must be called to initialize this instance immediately after invoking
     * the constructor. </p>
     *
     * @since 1.13
     */
    protected GetMapMarkers() {
      super(Mapmarkers.this, "GET", REST_PATH, null, com.example.pedro.myapplication.backend1.mapmarkers.model.MapMarkerCollection.class);
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public GetMapMarkers setAlt(java.lang.String alt) {
      return (GetMapMarkers) super.setAlt(alt);
    }

    @Override
    public GetMapMarkers setFields(java.lang.String fields) {
      return (GetMapMarkers) super.setFields(fields);
    }

    @Override
    public GetMapMarkers setKey(java.lang.String key) {
      return (GetMapMarkers) super.setKey(key);
    }

    @Override
    public GetMapMarkers setOauthToken(java.lang.String oauthToken) {
      return (GetMapMarkers) super.setOauthToken(oauthToken);
    }

    @Override
    public GetMapMarkers setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (GetMapMarkers) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public GetMapMarkers setQuotaUser(java.lang.String quotaUser) {
      return (GetMapMarkers) super.setQuotaUser(quotaUser);
    }

    @Override
    public GetMapMarkers setUserIp(java.lang.String userIp) {
      return (GetMapMarkers) super.setUserIp(userIp);
    }

    @Override
    public GetMapMarkers set(String parameterName, Object value) {
      return (GetMapMarkers) super.set(parameterName, value);
    }
  }

  /**
   * Builder for {@link Mapmarkers}.
   *
   * <p>
   * Implementation is not thread-safe.
   * </p>
   *
   * @since 1.3.0
   */
  public static final class Builder extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder {

    /**
     * Returns an instance of a new builder.
     *
     * @param transport HTTP transport, which should normally be:
     *        <ul>
     *        <li>Google App Engine:
     *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
     *        <li>Android: {@code newCompatibleTransport} from
     *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
     *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
     *        </li>
     *        </ul>
     * @param jsonFactory JSON factory, which may be:
     *        <ul>
     *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
     *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
     *        <li>Android Honeycomb or higher:
     *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
     *        </ul>
     * @param httpRequestInitializer HTTP request initializer or {@code null} for none
     * @since 1.7
     */
    public Builder(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
        com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      super(
          transport,
          jsonFactory,
          DEFAULT_ROOT_URL,
          DEFAULT_SERVICE_PATH,
          httpRequestInitializer,
          false);
    }

    /** Builds a new instance of {@link Mapmarkers}. */
    @Override
    public Mapmarkers build() {
      return new Mapmarkers(this);
    }

    @Override
    public Builder setRootUrl(String rootUrl) {
      return (Builder) super.setRootUrl(rootUrl);
    }

    @Override
    public Builder setServicePath(String servicePath) {
      return (Builder) super.setServicePath(servicePath);
    }

    @Override
    public Builder setHttpRequestInitializer(com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      return (Builder) super.setHttpRequestInitializer(httpRequestInitializer);
    }

    @Override
    public Builder setApplicationName(String applicationName) {
      return (Builder) super.setApplicationName(applicationName);
    }

    @Override
    public Builder setSuppressPatternChecks(boolean suppressPatternChecks) {
      return (Builder) super.setSuppressPatternChecks(suppressPatternChecks);
    }

    @Override
    public Builder setSuppressRequiredParameterChecks(boolean suppressRequiredParameterChecks) {
      return (Builder) super.setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
    }

    @Override
    public Builder setSuppressAllChecks(boolean suppressAllChecks) {
      return (Builder) super.setSuppressAllChecks(suppressAllChecks);
    }

    /**
     * Set the {@link MapmarkersRequestInitializer}.
     *
     * @since 1.12
     */
    public Builder setMapmarkersRequestInitializer(
        MapmarkersRequestInitializer mapmarkersRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(mapmarkersRequestInitializer);
    }

    @Override
    public Builder setGoogleClientRequestInitializer(
        com.google.api.client.googleapis.services.GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }
}
