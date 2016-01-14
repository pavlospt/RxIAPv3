[![Circle CI](https://circleci.com/gh/pavlospt/RxIAPv3/tree/master.svg?style=svg)](https://circleci.com/gh/pavlospt/RxIAPv3/tree/master)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-RxIAPv3-green.svg?style=true)](https://android-arsenal.com/details/1/2879)
[![Download](https://api.bintray.com/packages/pavlospt/android-libraries/RxIAPv3/images/download.svg)](https://bintray.com/pavlospt/android-libraries/RxIAPv3/_latestVersion)

# RxIAPv3
Android In-App Billing Library which provides several Rx Methods for Purchasing, Consuming and Listing Products
Based on the initial work of Anjlab (https://github.com/anjlab/android-inapp-billing-v3).

This library provides the existing functionality of Anjlab's initial work and as well a lot of helper methods that let you extract more information on the workflow of a purchase or a consume. It also uses Hawk (https://github.com/orhanobut/hawk) for the needs of caching products purchased and data of the purchases. 

This project is open to a lot of modifications to be better. I will try fixing a lot of things as the time goes by. I am also open to any pull request that can help the project :) 

How To Install
==============

This library is available on MavenCentral and on Bintray as well. All you need to do is add the following dependency on your application's ```build.gradle``` file.

```
compile 'com.github.pavlospt:rxiapv3:1.2'
```

How To Use
==========
Check the [Wiki Page](https://github.com/pavlospt/RxIAPv3/wiki) of the Library for instructions on how to use it.

Credits
=======
Author : Pavlos-Petros Tournaris (p.tournaris@gmail.com)

Google+ : [+Pavlos-Petros Tournaris](https://plus.google.com/u/0/+PavlosPetrosTournaris/)

Facebook : [Pavlos-Petros Tournaris](https://www.facebook.com/pavlospt)

LinkedIn : [Pavlos-Petros Tournaris](https://www.linkedin.com/pub/pavlos-petros-tournaris/44/abb/218)

(In case you use this in your app let me know to make a list of apps that use it! )

Library Credits
===============

Hawk Library : [Hawk](https://github.com/orhanobut/hawk)

Anjlab's Initial Library : [Anjlab IAB v3](https://github.com/anjlab/android-inapp-billing-v3)

License
=======

    Copyright 2015 Pavlos-Petros Tournaris

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
