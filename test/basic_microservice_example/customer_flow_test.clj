(ns basic-microservice-example.customer-flow-test
  (:require [basic-microservice-example.aux.init :as aux.init]
            [basic-microservice-example.components.mock-http :as mock-http]
            [basic-microservice-example.http-helpers :refer [GET POST]]
            [matcher-combinators.matchers :refer [in-any-order embeds equals]]
            [matcher-combinators.midje :refer [match]]
            [midje.sweet :refer :all]
            [postman.flow :refer [*world* flow defnq]]))

;; helpers
;; ------------------
;; postman transition (effectful)
(defn create-account! [{:keys [customer-id] :as world}]
  (let [customer-url (str "http://customer-service.com/customer/" customer-id)
        url          "/account/"
        resp-body    (mock-http/with-responses
                      [customer-url {:body {:customer-name "bob"}}]
                      (-> url
                          (POST {:customer-id customer-id} 200)
                          :body))]
    (assoc world
           :created-account resp-body
           :account-id      (-> resp-body :account :id))))

;; postman transition (effectful)
(defn delete-account! [{:keys [account-id] :as world}]
  (POST (str "/account/remove/" account-id) 200)
  world)

(defn lookup-account-from-customer-id
  [status {:keys [customer-id] :as world}]
  (let [url       (str "/account/from-customer/" customer-id)
        resp-body (:body (GET url status))]
    (assoc world :account-lookup resp-body)))

;; postman query: can be retried if following check fails
(defnq lookup-missing-account [world]
  (lookup-account-from-customer-id 400 world))

;; flows
;; ------------------
(flow "create savings account, look it up, and close it"
  aux.init/init!

  (fn [world] (assoc world :customer-id (java.util.UUID/randomUUID)))

  lookup-missing-account

  (fact "There shouldn't be a savings account for bob yet"
    (:account-lookup *world*) => {})

  create-account!

  (fact "There should now be a savings account for bob"
    (:created-account *world*) => (contains {:account (contains {:customer-id uuid?
                                                                 :id          uuid?
                                                                 :name        "bob"})}))

  (fact "There should now be a savings account for bob"
    (:created-account *world*) => (match {:account {:customer-id uuid?
                                                    :id          uuid?
                                                    :name        "bob"}}))


  delete-account!

  lookup-missing-account

  (fact "After deleting the savings account it can no longer be found"
    (:account-lookup *world*) => {}))



;; notes
;; ------------------
;; logging at each step
;; retry logic baked in (for e2e tests)
;; offers some measure of uniformity and re-use
