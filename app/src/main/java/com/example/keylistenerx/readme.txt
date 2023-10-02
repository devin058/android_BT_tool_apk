09-25 23:13:22.536 24476 24476 D AndroidRuntime: Shutting down VM
09-25 23:13:22.538 24476 24476 E AndroidRuntime: FATAL EXCEPTION: main
09-25 23:13:22.538 24476 24476 E AndroidRuntime: Process: com.example.keylistenerx, PID: 24476
09-25 23:13:22.538 24476 24476 E AndroidRuntime: java.lang.RuntimeException: Error receiving broadcast Intent { act=com.example.keylistenerx.broadcast flg=0x400010 (has extras) } in com.example.keylistenerx.MyBroadcast@537bf5a
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at android.app.LoadedApk$ReceiverDispatcher$Args.lambda$getRunnable$0$LoadedApk$ReceiverDispatcher$Args(LoadedApk.java:1710)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at android.app.LoadedApk$ReceiverDispatcher$Args$$ExternalSyntheticLambda0.run(Unknown Source:2)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at android.os.Handler.handleCallback(Handler.java:938)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at android.os.Handler.dispatchMessage(Handler.java:99)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at android.os.Looper.loopOnce(Looper.java:210)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at android.os.Looper.loop(Looper.java:299)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at android.app.ActivityThread.main(ActivityThread.java:8213)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at java.lang.reflect.Method.invoke(Native Method)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:556)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1045)
09-25 23:13:22.538 24476 24476 E AndroidRuntime: Caused by: android.os.FileUriExposedException: file:///storage/emulated/0/Download/aa.png exposed beyond app through ClipData.Item.getUri()
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at android.os.StrictMode.onFileUriExposed(StrictMode.java:2209)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at android.net.Uri.checkFileUriExposed(Uri.java:2402)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at android.content.ClipData.prepareToLeaveProcess(ClipData.java:1052)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at android.content.Intent.prepareToLeaveProcess(Intent.java:11388)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at android.content.Intent.prepareToLeaveProcess(Intent.java:11373)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at android.app.Instrumentation.execStartActivity(Instrumentation.java:1742)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at android.app.Activity.startActivityForResult(Activity.java:5533)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at android.app.Activity.startActivityForResult(Activity.java:5486)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at android.app.Activity.startActivity(Activity.java:5892)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at android.app.Activity.startActivity(Activity.java:5845)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at com.example.keylistenerx.OPP_Sender.send(OPP_Sender.java:62)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at com.example.keylistenerx.MyBroadcast.onReceive(MyBroadcast.java:75)
09-25 23:13:22.538 24476 24476 E AndroidRuntime:        at android.app.LoadedApk$ReceiverDispatcher$Args.lambda$getRunnable$0$LoadedApk$ReceiverDispatcher$Args(LoadedApk.java:1696)


这个错误是什么原因？
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setClassName("com.android.bluetooth", "com.android.bluetooth.opp.BluetoothOppLauncherActivity");
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);  //OPP_Sender.java:62
    }
}
由于成本原因公益站速率受到限制，付费站无限制

2023/9/25 23:16:06

这个错误是由于在Android 7.0（API级别24）及更高版本中引入的一项安全性改进所导致的。该改进禁止应用程序使用file://格式的Uri共享文件，以防止应用程序越权访问其他应用程序的私有文件。

在您的代码中，您正在尝试通过Uri.fromFile(file)创建一个file://格式的Uri，然后将其传递给Intent进行共享，但这在较新的Android版本中不再被允许。因此，当您调用startActivity(intent)时，系统会抛出FileUriExposedException。

要解决这个问题，您可以使用FileProvider来生成合法的content://格式的Uri，以便安全地共享文件。以下是修改后的示例代码：

java
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import androidx.core.content.FileProvider;

import java.io.File;

public class OPP_Sender {
    public void send(Context context, File file) {
        // 通过FileProvider获取合法的content:// Uri
        Uri contentUri = FileProvider.getUriForFile(context, "com.example.keylistenerx.fileprovider", file);

        // 授予URI临时的读取权限，以防其他应用程序需要访问该URI
        context.grantUriPermission("com.android.bluetooth", contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // 创建发送文件的Intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setClassName("com.android.bluetooth", "com.android.bluetooth.opp.BluetoothOppLauncherActivity");
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, contentUri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // 启动Intent
        context.startActivity(intent);
    }
}
在上述代码中，我们使用了FileProvider.getUriForFile()方法来获取一个合法的content://格式的Uri，并通过调用context.grantUriPermission()来授予其他应用程序临时的读取权限。这样，您就可以安全地共享文件了。

还需要注意，为了使FileProvider正常工作，您还需要在您的应用程序清单文件中进行相应的配置。请确保您已经按照Android官方文档中的说明进行了配置。

通过在AndroidManifest.xml文件中添加以下代码片段，创建一个<provider>元素：

xml
<application>
    ...
    <provider
        android:name="androidx.core.content.FileProvider"
        android:authorities="com.example.keylistenerx.fileprovider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_provider_paths" />
    </provider>
    ...
</application>
然后，在您的应用程序的res/xml/目录下创建一个名为file_provider_paths.xml的XML文件，并在其中指定要共享的文件路径。例如，如果您要共享应用程序的私有文件目录，可以使用以下内容：

xml
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <files-path name="files" path="." />
</paths>
请根据您的具体需求和文件路径进行相应的配置。这样，您就可以避免FileUriExposedException错误，并安全地共享文件了。

由于成本原因公益站速率受到限制，付费站无限制



2023/9/25 23:17:29

增加了com.example.keylistenerx.fileprovider需要修改manifest.xml吗
由于成本原因公益站速率受到限制，付费站无限制

2023/9/25 23:17:51

是的，增加了com.example.keylistenerx.fileprovider需要修改您的AndroidManifest.xml文件。您需要在<application>标签内添加一个<provider>元素，并为其指定相应的属性。以下是示例代码：

xml
<application>
    ...
    <provider
        android:name="androidx.core.content.FileProvider"
        android:authorities="com.example.keylistenerx.fileprovider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_provider_paths" />
    </provider>
    ...
</application>
请确保将android:authorities属性的值设置为com.example.keylistenerx.fileprovider，以便其他应用程序可以正确访问您共享的文件。另外，还需要创建一个名为file_provider_paths.xml的XML文件，并在其中定义要共享的文件路径。这些配置将确保FileProvider能够按预期工作，并解决FileUriExposedException错误。

请根据您的具体需求和文件路径进行相应的配置。

由于成本原因公益站速率受到限制，付费站无限制



2023/9/25 23:19:16

文件路径在/storage/emulated/0/Download/aa.png，也需要增加到file_provider_paths.xml吗？
由于成本原因公益站速率受到限制，付费站无限制

2023/9/25 23:19:35

是的，如果您要共享的文件路径是/storage/emulated/0/Download/aa.png，您需要将该路径添加到file_provider_paths.xml文件中。

在res/xml/目录下的file_provider_paths.xml文件中，您可以使用<external-path>元素来指定外部存储的根目录作为共享路径。以下是将/storage/emulated/0/Download/添加到file_provider_paths.xml的示例代码：

xml
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-path name="external_files" path="Download/" />
</paths>
请确保将path属性的值设置为Download/，这样共享的路径就会指向/storage/emulated/0/Download/目录。这样，您就可以安全地共享位于该路径下的文件了。

请注意，根据您的具体需求和文件路径，您可能需要进行更改或添加其他的<path>元素以适应不同的文件共享需求。

由于成本原因公益站速率受到限制，付费站无限制






