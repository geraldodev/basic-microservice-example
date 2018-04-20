(ns basic-microservice-example.adapters)

(defn str->uuid [id-str]
  (read-string (str "#uuid \"" id-str "\"")))

