create-cluster:
	gcloud dataproc clusters create hello --num-workers=3 --zone=us-east1-d
submit-job:
	gcloud dataproc jobs submit spark --cluster hello --class Hello --jar target/scala-2.11/Hello.jar
delete-cluster:
	gcloud dataproc clusters delete hello
.PHONY: create
