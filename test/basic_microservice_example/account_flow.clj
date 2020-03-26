(ns basic-microservice-example.account-flow
  (:require [basic-microservice-example.components :as components]
            [basic-microservice-example.components.mock-http :as mock-http]
            [basic-microservice-example.http-helpers :refer [GET POST]]
            [basic-microservice-example.protocols.storage-client :as storage-client]
            [matcher-combinators.matchers :refer [in-any-order embeds equals]]
            [matcher-combinators.midje :refer [match]]
            [midje.sweet :refer :all]
            [selvage.flow :refer [*world* flow]]))

(defn init!
  "Start the test system, which has the http component mocked, storing it in the world"
  [world]
  (let [system (components/ensure-system-up! :test-system)]
    (mock-http/clear-requests! (:http system))
    (storage-client/clear-all! (:storage system))
    (assoc world :system system)))

(defn create-account!
  "Create account by hitting /accounts/ endpoint.

   Since this endpoint gets customer info via an http request to another
   service, we mock the response from that service"
  [{:keys [customer-id] :as world}]
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

(defn lookup-missing-account
  "Assert that no account is found for the current customer ID by hitting the
   /account/from-customer/:customer-id endpoint"
  [{:keys [customer-id] :as world}]
  (let [url       (str "/account/from-customer/" customer-id)
        resp-body (:body (GET url 400))]
    (assoc world :account-lookup resp-body)))

(flow "create savings account, look it up, and close it"
  init!

  ;; create a customer ID
  (fn [world]
    (assoc world :customer-id (java.util.UUID/randomUUID)))

  ;; try to find an account that corresponds to that customer ID
  lookup-missing-account

  (fact "There shouldn't be a savings account for Bob yet"
    (:account-lookup *world*) => {})

  create-account!

  (fact "There should now be a savings account for Bob"
    (:created-account *world*) => (match {:account (equals {:customer-id uuid?
                                                            :id          uuid?
                                                            :name        "bob"})})))
