# ProgressView
Some Progress Views for Android

## Download

Gradle:
```groovy
compile 'ru.bullyboo.progress:android:1.0.1'
```

```xml
<dependency>
  <groupId>ru.bullyboo.progress</groupId>
  <artifactId>android</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```

## About
This library is a collection of Progress Views for Android. You will find here the most popular views in future.

## ProgressView
ProgressView has two line modes:

#### Circle:

![](https://github.com/BullyBoo/ProgressView/blob/master/screenshots/Screenshot_2.png)

#### Square:

![](https://github.com/BullyBoo/ProgressView/blob/master/screenshots/Screenshot.png)

### Animation
Also you can turn on the animation of changing progress.

#### Circle:

With animation:

![](https://github.com/BullyBoo/ProgressView/blob/master/screenshots/circle_with_anim.gif)

Without animation:

![](https://github.com/BullyBoo/ProgressView/blob/master/screenshots/circle_without_anim.gif)

#### Square:

With animation:

![](https://github.com/BullyBoo/ProgressView/blob/master/screenshots/square_with_anim.gif)

Without animation:

![](https://github.com/BullyBoo/ProgressView/blob/master/screenshots/square_without_anim.gif)

### Attributes

`backgroundLineWidth` - width of background line, that is drawn behind of progress line

`progressLineWidth` - widht of progress line

`backgroundLineColor` and `progressLineColor` - the colors of lines

`max` - max value of progress

`min` - min value of progress

Notice, `min` can't be less or equal to `max` value.

`progress` - current value of progress. This value must be between or equal to `min` or `max` value.

`lineMode` - this attribute sets the mode of line. Now you can choose `cirle` or `square`.

`animateProgress` - flag, that enable animation of changing progress.

`animationDuration` - sets the duration of animation.

`mode` - sets `horizontal` or `vertical` mode of progress line

`reverse` - sets the reverse mode of progress line

## License
```
  Copyright (C) 2017 BullyBoo

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
