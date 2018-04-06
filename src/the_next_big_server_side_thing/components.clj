(ns the-next-big-server-side-thing.components
  (:refer-clojure :exclude [test])
  (:require [com.stuartsierra.component :as component]
            [the-next-big-server-side-thing.components.dummy-config :as config]
            [the-next-big-server-side-thing.service :as the-next-big-server-side-thing.service]
            [the-next-big-server-side-thing.components.dev-servlet :as dev-servlet]
            [the-next-big-server-side-thing.components.mock-servlet :as mock-servlet]
            [the-next-big-server-side-thing.components.mock-http :as mock-http]
            [the-next-big-server-side-thing.components.service :as service]
            [the-next-big-server-side-thing.components.routes :as routes]
            [the-next-big-server-side-thing.components.http-kit :as http-kit]
            [the-next-big-server-side-thing.components.system-utils :as system-utils]
            [the-next-big-server-side-thing.components.http :as http]
            [schema.core :as s]))

(def webapp-deps [:http])

(def base-config-map {:environment :prod
                      :dev-port    8080})

(def local-config-map {:environment :dev
                       :dev-port    8080})

(defn base []
  (component/system-map
    :config    (config/new-config base-config-map)
    :http-impl (component/using (http-kit/new-http-client) [:config])
    :http      (component/using (http/new-http) [:config :http-impl])
    :routes    (routes/new-routes #'the-next-big-server-side-thing.service/routes)
    :service   (component/using (service/new-service) [:config :routes :http])
    :servlet   (component/using (dev-servlet/new-servlet) [:service])))

(defn e2e []
  (s/set-fn-validation! true)
  (merge (base)
         (component/system-map
           :config (config/new-config local-config-map))))

(defn test-system []
  (merge (base)
         (component/system-map
           :config  (config/new-config local-config-map)
           :servlet (component/using (mock-servlet/new-servlet) [:service])
           :http    (component/using (mock-http/new-mock-http) [:config])
           )))

(def systems-map
  {:e2e-system   e2e
   :local-system e2e
   :test-system  test-system
   :base-system  base})

(defn create-and-start-system!
  ([] (create-and-start-system! :base-system))
  ([env]
   (system-utils/bootstrap! systems-map env)))

(defn ensure-system-up! [env]
  (or (deref system-utils/system)
      (create-and-start-system! env)))

(defn stop-system! [] (system-utils/stop-components!))
