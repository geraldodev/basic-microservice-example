(ns basic-microservice-example.components.http
  (:require [com.stuartsierra.component :as component]
            [basic-microservice-example.http.serialization :as serialization]
            [basic-microservice-example.protocols.http-client :as h-pro]))

(defn- render-body
  "If we have a payload, use the serialize function from the request map to
   convert it to external"
  [{:keys [payload serialize] :as req-map}]
  (if payload (assoc req-map :body (serialize payload)) req-map))

(defn render-req [default-req-map req-map]
  (-> (merge default-req-map req-map) ; allow defaults to be overridden
      render-body))                   ; serialize the request body

(defn- request-sync!
  "add the response details to the request map"
  [req-map http-impl]
  (assoc req-map :response (h-pro/req! http-impl req-map)))

(defn- parse-body
  "If we have a body, use the deserialize function from the request map to
   convert it back to internal"
  [{:keys [deserialize response] :as resp-map}]
  (cond
    (-> response :body nil?)
    resp-map

    (-> response :body string? not)
    (update-in resp-map [:response :body] (comp deserialize slurp))

    (:body response)
    (update-in resp-map [:response :body] deserialize)

    :else
    resp-map))

(defn handle-response [resp-map]
  (-> resp-map
      parse-body                               ; parse the body and return the response
      :response                                ; unwrap the repsonse only from the resp-map
      (select-keys [:status :body :headers]))) ; drop excess http implementation keys

(defn do-req-resp! [request {:keys [http-impl] :as component}]
  (-> request
      (request-sync! http-impl)
      handle-response)) ; parse and return the response

(defrecord Http [defaults http-impl]
  ;; There are two arities of the req! protocol method to allow for more specific DSLs
  ;; Component starts with a default request map
  ;; This default map can be overridden on a per-request basis

  h-pro/HttpClient
  (req! [this req-map]
    (h-pro/req! this defaults req-map))
  (req! [this default-req-map req-map]
    (let [request (render-req default-req-map req-map)]
      (do-req-resp! request this)))

  component/Lifecycle
  (start [this] this)
  (stop  [this] this)

  Object
  (toString [_] "<Http>"))

(defmethod print-method Http [_ ^java.io.Writer w]
  (.write w "<Http>"))

(def default-headers
  {"Content-Type"    "application/json; charset=utf-8"
   "Accept-Encoding" "gzip, deflate"})

(def json-defaults
  {:method             :get
   :user-agent         "http-kit / your org"
   :headers            default-headers
   :serialize          serialization/write-json
   :deserialize        serialization/read-json
   :timeout            30000   ; 30 second timeout
   :keepalive          120000  ; 120 second keepalive
   :follow-redirects   false
   :as                 :text})

(defn new-http
  ([] (new-http json-defaults))
  ([defaults] (map->Http {:defaults defaults})))
