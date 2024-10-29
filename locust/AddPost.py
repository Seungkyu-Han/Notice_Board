from locust import HttpUser, TaskSet, task, between
import random

class AddPost(HttpUser):
    wait_time = between(1, 2)
    @task
    def add_post(self):
        self.client.post("http://localhost:8081/posts",
                         headers={
                             "Content-Type": "application/json",
                             "Authorization": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6InNldW5na3l1Iiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3MzAxODA4MTAsImV4cCI6MTczMDE4ODAxMH0.vbbFunXJASqhoegoWf9l2oV0V5JPthwREsS4vUpocfs"},
                         json={"name": "testName", "content": "testContent", "categoryId": "671f019a1e03573103c881ce"})

