(ns the-next-big-server-side-thing.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [the-next-big-server-side-thing.controller :as controller]
            [the-next-big-server-side-thing.interceptors.error-info :as error-info]
            [ring.util.response :as ring-resp]))

(defn about-page
  [request]
  (ring-resp/response {:version (format "Clojure %s - served from %s"
                                        (clojure-version)
                                        (route/url-for ::about-page))}))

(defn customer-page
  [request]
  (let [{:keys [http]} (:components request)
        customer       (controller/get-customer! "bob" http)]
    (ring-resp/response customer)))

(defn home-page
  [request]
  (ring-resp/response "Hello World!"))

(def common-interceptors
  [(body-params/body-params) http/html-body error-info/log-error-during-debugging])

;; Tabular routes
(def routes #{["/" :get (conj common-interceptors `home-page)]
              ["/customer" :get (conj common-interceptors `customer-page)]
              ["/about" :get (conj common-interceptors `about-page)]})
