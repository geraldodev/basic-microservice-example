(ns the-next-big-server-side-thing.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [the-next-big-server-side-thing.controller :as controller]
            [ring.util.response :as ring-resp]))

(defn about-page
  [request]
  (ring-resp/response (format "Clojure %s - served from %s"
                              (clojure-version)
                              (route/url-for ::about-page))))

(defn customer-page
  [request]
  (clojure.pprint/pprint request)
  (let [{:keys [http]} (:components request)
        customer       (controller/get-customer! "bob" http)]
    (ring-resp/response customer)))

(defn home-page
  [request]
  (ring-resp/response "Hello World!"))

;; Defines "/" and "/about" routes with their associated :get handlers.
;; The interceptors defined after the verb map (e.g., {:get home-page}
;; apply to / and its children (/about).
(def common-interceptors [(body-params/body-params) http/html-body])

;; Tabular routes
(def routes #{["/" :get (conj common-interceptors `home-page)]
              ["/customer" :get (conj common-interceptors `customer-page)]
              ["/about" :get (conj common-interceptors `about-page)]})
