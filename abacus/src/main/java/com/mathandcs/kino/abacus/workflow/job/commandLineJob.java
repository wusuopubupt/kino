package com.mathandcs.kino.abacus.workflow.job;

import com.mathandcs.kino.abacus.exception.JobExecutionException;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by dashwang on 6/18/2017.
 * <p>
 *
 * @brief: execute a shell command
 * @return JobId
 */
@Data
public class commandLineJob extends Job {

    private String command;
    private Logger logger = LoggerFactory.getLogger(commandLineJob.class);

    @Override
    public void run() {
        ExecutorResult result = execute(command);
    }

    @Override
    public void cancel() throws Exception {

    }

    @Data
    public static class ExecutorResult {
        private int exitCode;
        private String stdOut;
        private String stdErr;
    }

    private ExecutorResult execute(String... command) throws JobExecutionException {

        ExecutorResult result = new ExecutorResult();
        StringBuffer stdOutBuffer = new StringBuffer();
        StringBuffer stdErrBuffer = new StringBuffer();

        try {

            Process p = Runtime.getRuntime().exec(command);

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                stdOutBuffer.append(line + "\n");
                logger.info(line);
            }

            reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            line = "";
            while ((line = reader.readLine()) != null) {
                stdErrBuffer.append(line + "\n");
                logger.error(line);
            }

            p.waitFor();

            result.setStdOut(stdOutBuffer.toString());
            result.setStdErr(stdErrBuffer.toString());
            result.setExitCode(p.exitValue());

        } catch (Exception e) {
            throw new JobExecutionException("Command execution failed.", e);
        }

        return result;
    }

}

