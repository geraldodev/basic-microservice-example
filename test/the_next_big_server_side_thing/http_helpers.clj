(ns the-next-big-server-side-thing.http-helpers
  (:require [io.pedestal.test :refer [response-for]]
            [io.pedestal.http :as bootstrap]
            [the-next-big-server-side-thing.components.system-utils :as system-utils]))

(defn bootstrap-service []
  ;; Bootstrap a test version of a Pedestal service
  (-> :servlet system-utils/get-component! :instance ::bootstrap/service-fn))

(defn req!
  ([method headers uri expected-status]
   (req! method headers uri nil expected-status))
  ([method headers uri body expected-status]
   ;; Raw pedestal response, without content negotiation or serialization support
   (let [service  (bootstrap-service)
         _ (println "SERVICE\t" service)
         response (response-for
                    service method uri :body body :headers headers)]
     (when-not (= expected-status (:status response))
       (throw (ex-info "HTTP request resulted in unexpected return status"
                       {:expected-status expected-status
                        :response-status (:status response)
                        :uri             uri})))
     response)))

(def default-headers
  {"Content-Type"    "text/plain ;charset=utf-8,*/*"
   "Accept-Encoding" "gzip, deflate"})

(def GET  (partial req! :get default-headers))
(def POST (partial req! :post default-headers))
