(ns the-next-big-server-side-thing.components.service
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.interceptor.helpers :refer [before]]
            [io.pedestal.http.route :as route]
            [io.pedestal.http :as bootstrap]))

(defn- add-system [service]
  (before (fn [context] (assoc-in context [:request :components] service))))

(defn system-interceptors [service-map service]
  (update-in service-map 
             [::bootstrap/interceptors]
             #(vec (->> % (cons (add-system service))))))

(defn base-service [routes port]
  {:env                        :prod
   ::bootstrap/router          :prefix-tree
   ::bootstrap/routes          #(route/expand-routes (deref routes))
   ::bootstrap/resource-path   "/public"
   ::bootstrap/type            :jetty
   ::bootstrap/port            port})

(defn prod-init [service-map]
  (bootstrap/default-interceptors service-map))

(defn dev-init [service-map]
  (-> service-map
      (merge {:env                        :dev
              ;; do not block thread that starts web server
              ::bootstrap/join?           false
              ;; Content Security Policy (CSP) is mostly turned off in dev mode
              ::bootstrap/secure-headers {:content-security-policy-settings {:object-src "none"}}
              ;; all origins are allowed in dev mode
              ::bootstrap/allowed-origins {:creds true :allowed-origins (constantly true)}})
      ;; Wire up interceptor chains
      bootstrap/default-interceptors
      bootstrap/dev-interceptors))

(defn runnable-service [config routes service]
  (let [env          (:environment config)
        port         (:dev-port config)
        service-conf (base-service routes port)]
    (-> (if (= :prod env)
          (prod-init service-conf)
          (dev-init service-conf))
        (system-interceptors service))))

(defrecord Service [config routes]
  component/Lifecycle
  (start [this]
    (assoc this
           :runnable-service
           (runnable-service (:config config) (:routes routes) this)))

  (stop [this]
    (dissoc this :runnable-service))

  Object
  (toString [_] "<Service>"))

(defmethod print-method Service [v ^java.io.Writer w]
  (.write w "<Service>"))

(defn new-service [] (map->Service {}))
