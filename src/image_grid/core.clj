(ns image-grid.core
  (:require [clojure.math :as math]
            [mikera.image.core :as mk]
            [mikera.image.colours :as mkcolors]
            [image-grid.color :as color]))

;; Images represented as 2D vectors of 3-tuples of floats between 0.0 and 1.0.
;; Creating functions to manipulate those in an immutable fashion, and then
;; only converting to an image at the end.

;; TODO Image library:
;; - rgb-to-hsv

;; TODO for GP:
;; - move center to middle of image
;; - make coordinates so that 1/2 longer dimension = 1. Will need to remove / 100 from rgb-fns-pixel
;; - add polar coordinates to color function inputs
;; - GP???

(defn new-image-grid
  "Creates a new 2D vector of given width and height,
   filled with black pixels, i.e. [0.0 0.0 0.0]."
  [width height]
  (vec (repeat height
               (vec (repeat width [0.0 0.0 0.0])))))

(defn get-pixel
  "Gets a pixel from a given location in image-grid"
  [image-grid [x y]]
  (get-in image-grid [y x]))

(defn set-pixel
  "Sets pixel at given location in image-grid to rgb tuple"
  [image-grid [x y] rgb]
  (assert (coll? rgb))
  (assert (= 3 (count rgb)))
  (assoc-in image-grid [y x] rgb))

(defn map-image-grid
  "Given a pixel-fn and an image-grid, apply the pixel-fn to each pixel in the
   image, returning a new image grid.
   pixel-fn should take as its parameters
   an x-y pair of the pixel's location and an RGB tuple of the pixel's current
   color, both of which are vectors.
   pixel-fn should return an [r g b] tuple."
  [pixel-fn image-grid]
  (reduce (fn [igrid xy]
            (set-pixel igrid
                       xy
                       (pixel-fn xy (get-pixel image-grid xy))))
          image-grid
          (for [x (range (count (first image-grid)))
                y (range (count image-grid))]
            [x y])))

(defn mkimage->image-grid
  "Given a mkimage, returns the corresponding image grid."
  [mkimage]
  (let [width (.getWidth mkimage)
        height (.getHeight mkimage)
        initial-grid (new-image-grid width height)]
    (map-image-grid (fn [[x y] _]
                      (mapv #(/ % 255.0)
                            (mkcolors/components-rgb
                             (mk/get-pixel mkimage x y))))
                    initial-grid)))

(defn image-grid->mkimage
  "Makes a new mikera image and sets its pixels from image-grid"
  [image-grid]
  (let [rows (count image-grid)
        cols (count (first image-grid))
        mk-image (mk/new-image cols rows)]
    (doseq [x (range cols)
            y (range rows)]
      (mk/set-pixel mk-image x y (apply mkcolors/rgb (get-pixel image-grid [x y]))))
    mk-image))

;; Examples using the image-grid/pixel functions.
(comment

  ;; Create a new image with a reddish pixel and a short green line.
  (let [img (-> (new-image-grid 50 30)
                (set-pixel [10 5] [0.8 0.0 1.0])
                (set-pixel [25 20] [0.0 0.9 0.1])
                (set-pixel [26 20] [0.0 0.9 0.1])
                (set-pixel [27 20] [0.0 0.9 0.1])
                (set-pixel [28 20] [0.0 0.9 0.1])
                (set-pixel [29 20] [0.0 0.9 0.1])
                (image-grid->mkimage))]
    (mk/save img "images/test.png") ;; save image as test.png 
    (mk/show img))           ;; show the image

  ;; Make an image filled with a gradient using map-image-grid
  (mk/show
   (let [width 500
         height 300
         empty-image (new-image-grid width height)
         pixel-fn (fn [[x y] _]
                    [(/ y height) (/ x width) 0.3])
         img-grid (map-image-grid pixel-fn empty-image)]
     (image-grid->mkimage img-grid)))

  ;; Load an image and make it redder
  (mk/show
   (image-grid->mkimage
    (map-image-grid
     (fn [_ [r g b]]
       [(+ r 0.2) g b])
     (mkimage->image-grid (mk/load-image "images/dog.jpg")))))

  )



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; Making an image from functions

(defn logistic
  "Logistic/sigmoid function."
  [z]
  (try (/ 1.0
          (inc (math/exp (- z))))
       (catch Exception _ 0.0)))

(defn rgb-fns->pixel-fn
  "Given three functions that transform x-y pairs to r, g, and b respectively,
   creates a pixel-fn as required by map-image-grid.
   
   Still working out some kinks, just added HSV, and need to change the
   / 100 once the image coordinates are different."
  [r-fn g-fn b-fn]
  (fn [[x y] _]
    (color/hsv->rgb (mapv #(logistic (/ (% x y) 100.0))
                    [r-fn g-fn b-fn]))))

(defn rgb-fns-to-image
  "Given width and height of an image, as well as function specifying how to
   transform each pixel's coordinates into r, g, and b values, creates
   a new image with those pixels."
  [width height r-fn g-fn b-fn]
  (let [new-image (new-image-grid width height)]
    (image-grid->mkimage
     (map-image-grid (rgb-fns->pixel-fn r-fn g-fn b-fn)
                     new-image))))


(defn ex-r-fn
  [x y]
  (- x y)
  #_(* 400 (+ -200 (+ (* 0.5 x) y))))

(defn ex-g-fn
  [x y]
  (* (- x 100) (- y 50)))

(defn ex-b-fn
  [x _]
  (- (mod (* x 1.5) 400) 200))

(comment

  (mk/show (rgb-fns-to-image 400 400 ex-b-fn ex-g-fn ex-r-fn)))
