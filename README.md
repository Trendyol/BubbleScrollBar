<img src="https://raw.githubusercontent.com/Trendyol/BubbleScrollBar/master/art/bubble.png"/>

# Usage

```xml
<com.trendyol.bubblescrollbarlib.BubbleScrollBar
            android:id="@+id/bubbleScrollBar"
            android:layout_width="wrap_content"
            android:layout_height="match_parent"
            android:layout_gravity="end"
            android:orientation="vertical"
            app:scrollbarBackground="#40D81B60"
            app:thumbBackground="@color/colorAccent"
            app:bubbleBackground="@drawable/fast_scroll_thumb"
            app:bubbleTextSize="8sp"
            app:bubbleElevation="6dp"
            app:bubbleMargin="4dp"
            app:bubbleHeight="50dp"
            app:bubbleMinWidth="50dp"
            app:bubbleTextColor="@color/default_bubble_text_color"
            app:bubblePadding="4dp"/>
```

```kotlin
bubbleScrollBar.attachToRecyclerView(recyclerView)
bubbleScrollBar.bubbleTextProvider = BubbleTextProvider { sampleAdapter.data[it] }
```

# Dependency 
```groovy
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}

dependencies {
      implementation 'com.github.Trendyol:BubbleScrollBar:0.1'
}
```

License
--------


    Copyright 2018 Trendyol.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



