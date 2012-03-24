# iOS Download Attribution

Vungle is providing a .h and .m file that will easily handle download reporting for you.

## VGDownload iOS Module

This module can be added to your iOS application to track app downloads. To use it, drag VGDownload.m and VGDownload.h into your project, and call the function VGReportDownload() from your application delegate's startup method, which likely goes by this name:

```Obj-c
-(BOOL)application:(UIApplication*)application didFinishLaunchingWithOptions:(NSDictionary*)launchOptions
```

