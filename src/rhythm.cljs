(ns rhythm)

(defn- create-action
  [action]
  {:action action})

(defn- create-state
  [in out]
  {:in in
   :out out})

(defn actor
  "Create actor"
  [name {:keys [states data]
         :or {states []
              data {}}}]
  (atom {:name name
         :current (set states)
         :watchers {}
         :states {}
         :actions {}
         :data data}))

(defn actor-name
  ""
  [a]
  (:name @a))

(defn actor-data
  ""
  ([a]
     (:data @a))
  ([a key]
     (-> (:data @a)
         key)))

(defn reset-actor-data!
  [a key val]
  (let [target (-> (actor-data a)
                   key)]
    (reset! target val)))

(defn- can-assoc?
  [a key]
  ((complement contains?) (:current a) key))

(defn- can-dissoc?
  [a key]
  (contains? (:current a) key))

(defn is-state?
  ""
  [a key]
  (contains? (:current @a) key))

(defn- assoc-state
  [a key]
  (assoc a :current
         (conj (:current a) key)))

(defn- dissoc-state
  [a key]
  (assoc a :current
         (disj (:current a) key)))

(defn- assoc-watcher
  [a key in out]
  (assoc a :watchers
         (merge-with concat (:watchers a) {key [{:in in
                                                 :out out}]})))

(defn- dissoc-watcher
  [a key]
  (assoc a :watchers
         (dissoc (:watchers a) key)))

(defmulti add-state (fn [a k inout] (cond
                                     (coll? k) :multiple
                                     (keyword? k) :single)))

(defmethod add-state :single
  [a key {:keys [in out]
          :or {in #()
               out #()}}]
  (swap! a assoc-in [:states key] (create-state in out))
  a)

(defmethod add-state :multiple
  [a keys inout]
  (doseq [k keys]
    (add-state a k inout))
  a)

(defn add-action
  ""
  [a key action]
  (swap! a assoc-in [:actions key] (create-action action))
  a)

(defn watch-state!
  ""
  [a key {:keys [in out]
          :or {in #()
               out #()}}]
  (swap! a assoc-watcher key in out))

(defn unwatch-state!
  ""
  [a key]
  (swap! a dissoc-watcher key))

(defn- get-state
  [a key]
  (get (:states @a) key))

(defn- get-action
  [a key]
  (get (:actions @a) key))

(defn- get-watchers
  [a key]
  (get (:watchers @a) key))

(defn on!
  ""
  [a key & args]
  (let [e (get-state a key)
        in (:in e)
        watchers (get-watchers a key)]
    (swap! a assoc-state key)
    (when-not (nil? in)
      (apply in args))
    (when-not (nil? watchers)
      (doseq [w watchers]
        (apply (:in w) args)))))

(defn off!
  ""
  [a key & args]
  (let [e (get-state a key)
        out (:out e)
        watchers (get-watchers a key)]
    (swap! a dissoc-state key)
    (when-not (nil? out)
      (apply out args))
    (when-not (nil? watchers)
      (doseq [w watchers]
        (apply (:out w) args)))))

(defn switch-state!
  ""
  [a pre-key key & args]
  (when-not (nil? pre-key)
    (apply off! (concat [a pre-key] args)))
  (apply on! (concat [a key] args)))

(defn trigger-action!
  ""
  [a key & args]
  (let [e (get-action a key)
        action (:action e)]
    (if-not (nil? action)
      (apply action args)
      (throw (js/Error. (str "Action is not defined: " (pr-str key)))))))
