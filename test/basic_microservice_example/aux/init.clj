(ns basic-microservice-example.aux.init
  (:require [basic-microservice-example.components :as components]
            [basic-microservice-example.protocols.storage-client :as storage-client]
            [basic-microservice-example.components.system-utils :as system-utils]
            [basic-microservice-example.components.mock-http :as mh-com]))

(defn init! [world]
  (let [system (components/ensure-system-up! :test-system)]
    (mh-com/clear-requests! (:http system))
    (storage-client/clear-all! (:storage system))
    (-> world
        (assoc :system system))))
