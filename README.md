# BibleLibrary
A simple Library to show bible marked up like links, this is based on the orignal And Bible but i have to modifyit to suit the need i have based on the project i was currently working on

#Usage
Simply add the following lines to your build.gradle
````
dependencies {
compile 'com.bellman:bible:1.2.1'
}
````
The call the instance of the Bible Class in your Applications Oncreate() as follows:
````
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Bible.getInstance().initAll(this);
    }
}
````
###Follow this step an insert any String you would like to call the Bible Api
````
 TextView testTv = (TextView) findViewById(R.id.test);
  testTv.setText(Html.fromHtml("<a href='bible://bible.get/Matt/2/5'>Matthew 2:5</a>"));
  testTv.setMovementMethod(LinkMovementMethod.getInstance());
````

Format: ![Alt Text](https://github.com/jerryhanks/BibleLibraryTest/blob/master/screenshots/Screenshot_2.png)

#Want to Contribut or Make Bible Api better
Fork the repo and create a pull request of your latest changes so that we can merge and move on.
