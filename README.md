image-grid
==========

A very simple image processing library for Clojure, based on immutable image-grids. Designed to only support simple pixel-based manipulations of images in RGB or HSV color spaces. Relies on the library net.mikera/imagez for everything besides the manipulations of images themselves.

## Example

Until I get this pushed to Clojars, you can add the dependency through GitHub. Your `deps.edn` file should look something like this:

```clojure
{:deps
 {net.mikera/imagez {:mvn/version "0.12.0"} 
  io.github.thelmuth/image-grid {:git/sha "6ed4021d0cb38d2dfcce71592fa5b56176adce64"}}}
```

Now you'll want a namespace something like this at the top of your program:

```clojure
(ns my-project.core
  (:require [mikera.image.core :as mk]
            [image-grid.core :as ig]
            [image-grid.color :as color]))
```

And now you can use and manipulate image grids:

```clojure
  (mk/show
   (let [width 500
         height 300
         empty-image (ig/new-image-grid width height)
         pixel-fn (fn [[x y] _]
                    [(/ y height) (/ x width) 0.3])
         img-grid (ig/map-image-grid pixel-fn empty-image)]
     (ig/image-grid->mkimage img-grid)))
```

Which will generate and show this image:

![Gradient](https://github.com/thelmuth/image-grid/blob/main/images/ig-gradient.png)

See `src/image_grid/demo.clj` for more examples.