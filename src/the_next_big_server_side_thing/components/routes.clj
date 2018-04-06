(ns the-next-big-server-side-thing.components.routes
  (:require [com.stuartsierra.component :as component]))

(defrecord Routes [routes]
  component/Lifecycle
  (start [this]
    (assoc this :routes routes))
  (stop  [this] (dissoc this :routes)))

(defn new-routes [routes] (map->Routes {:routes routes}))
