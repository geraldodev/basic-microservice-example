(ns basic-microservice-example.aux.init
  (:require [basic-microservice-example.components :as components]
            [basic-microservice-example.protocols.storage-client :as storage-client]
            [basic-microservice-example.components.mock-http :as mock-http]))

(defn init! [world]
  (let [system (components/ensure-system-up! :test-system)]
    (mock-http/clear-requests! (:http system))
    (storage-client/clear-all! (:storage system))
    (-> world
        (assoc :system system))))
