# android Download Attribution

1. Download the source by cloning this repo, or by using this link: (https://github.com/downloads/Vungle/AdvertiserSDK/VungleAndroidDownloadsSrc-1.0.zip)
1. Drop the 'src' folder to your local project
1. Remove the "Demo.java" from the src if you don't want usage examples.
2. In your main application class, add this to the onCreate method:
```
Vungle.init(this);
```

3. Visit your dashboard and check if installs get reported for your app. If you don't see them appear, check if you've set up the correct Application ID in the dashboard.
