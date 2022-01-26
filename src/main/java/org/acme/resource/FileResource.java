package org.acme.resource;

import io.agroal.api.AgroalDataSource;
import org.acme.global.Globals;
import org.acme.task.Task;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Path("/f")
public class FileResource {
    private static Logger LOGGER = Logger.getLogger(FileResource.class.getName());

    @Inject
    AgroalDataSource dataSource;

    @Inject
    Globals globals;

    @GET
    @Path("/files")
    @Produces(MediaType.APPLICATION_JSON)
    public Response downloadFileRequest() {
        Runnable r1 = new Task(dataSource);
        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.execute(r1);
        pool.shutdown();

        return Response.ok().build();
    }

    @GET
    @Path("/files/status/{filename}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFileStatus(@PathParam("filename") String fileName) {
        LOGGER.info("fileStatus: " + globals.getFileStatus(fileName));
        return Response.ok(Globals.getFileStatus(fileName)).build();
    }

    @GET
    @Path("/files/download/{filename}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("filename") String fileName) {
        if(globals.getFileStatus(fileName) == "done") {
            File fileDownload = new File("D:/java/report-downloader/" + fileName);
            LOGGER.info("File: " + fileName);
            LOGGER.info("file exists: " + fileDownload.exists());
            Response.ResponseBuilder response = Response.ok((Object) fileDownload);
            response.header("Content-Disposition", "attachment;filename=" + fileName);
            return response.build();
        }
        return Response.status(400).build();
    }
}