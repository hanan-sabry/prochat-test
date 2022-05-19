package com.app.chattestapp;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class AttachListenerWorker extends Worker {

    private Context context;

    public AttachListenerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);


    }

    @NonNull
    @Override
    public Result doWork() {
        Toast.makeText(context, "I'm doing my work now", Toast.LENGTH_SHORT).show();
        return Result.success();
    }
}
