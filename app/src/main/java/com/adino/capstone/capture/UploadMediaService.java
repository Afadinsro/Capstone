package com.adino.capstone.capture;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by afadinsro on 3/12/18.
 */

public class UploadMediaService extends JobService {

    @Override
    public boolean onStartJob(JobParameters job) {
        return false; // Is there still more work to do?
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // Should the job be retried?
    }
}
