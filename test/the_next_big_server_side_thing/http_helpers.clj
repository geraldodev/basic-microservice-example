(ns the-next-big-server-side-thing.http-helpers
  (:require [io.pedestal.test :refer [response-for]]
            [io.pedestal.http :as bootstrap]
            [the-next-big-server-side-thing.http.serialization :as serialization]
            [the-next-big-server-side-thing.components.system-utils :as system-utils]))

(defn bootstrap-service []
  ;; Bootstrap a test version of a Pedestal service
  (-> :servlet system-utils/get-component! :instance ::bootstrap/service-fn))

(def default-headers
  {"Content-Type"    "application/edn ;charset=utf-8,*/*"
   "Accept-Encoding" "gzip, deflate"})

(defn output-stream->data [output]
  (if (string? output)
    (serialization/read-edn output)
    output))

(defn req!
  ([method uri expected-status]
   (req! method uri nil expected-status))
  ([method uri body expected-status]
   ;; Raw pedestal response, without content negotiation or serialization support
   (let [service         (bootstrap-service)
         {:keys [headers
                 status
                 body]}  (response-for service method uri
                                       :body (when body
                                               (serialization/write-edn body))
                                       :headers default-headers)]
     (let [deserialized-resp {:body   (try (output-stream->data body)
                                           (catch Exception _ body))
                              :headers headers}]
       (assert (= status expected-status)
               (str method " request to '" uri "' expected status of "
                    expected-status ", but received " status " with response: " deserialized-resp))
       deserialized-resp))))

(def GET  (partial req! :get))
(def POST (partial req! :post))
