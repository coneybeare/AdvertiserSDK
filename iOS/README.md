# iOS Download Attribution

Vungle is providing a few source files that will easily handle download reporting for you.

## VGDownload iOS Module

This module can be added to your iOS application to track app downloads. To use
it, drag these four files into your project:

* VGDownload.m
* VGDownload.h
* OpenUDID.m
* OpenUDID.h
  
Call the function VGReportDownload() from your
application delegate's startup method, which likely goes by this name:

```Obj-c
-(BOOL)application:(UIApplication*)application didFinishLaunchingWithOptions:(NSDictionary*)launchOptions
```

