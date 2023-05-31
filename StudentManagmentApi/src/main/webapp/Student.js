
function addStudent() {
   var age = document.getElementById('age').value;
var name = document.getElementById('name').value;

 // disable the create butoon to mutiple submissions
 document.getElementById('createStudent').disabled=true;

 //display a loading message  in progress
 document.getElementById('statusMessage').innerHTML='Creating student...'

    if(age>5 && age<50){
      alert(`adding detials : Name - ${name} - age : ${age}`);

     
     var xhr=new XMLHttpRequest();
     xhr.open('POST','/students-details/',true);

     xhr.setRequestHeader('Content-Type','application/json');

     xhr.onreadystatechange=function(){
      if(xhr.readyState===XMLHttpRequest.DONE){
         if(xhr.status==201){
            
            //Display the succes message
            document.getElementById('statusMessage').innerHTML="Student created successfully";

            //clear the input fields
            document.getElementById('name').value= '';
            document.getElementById('age').value= '';

            //Re-enabel the addbutton
            document.getElementById('createStudent').disabled=false;
            var responseData = JSON.parse(xhr.responseText);

        // Get a reference to the student table body
        var tableBody = document.getElementById('student-table-body');

        // Create a new table row for the student data
        var newRow = document.createElement('tr');

        // Create new table cells for each data field
        var nameCell = document.createElement('td');
        nameCell.textContent = responseData.name;

        var ageCell = document.createElement('td');
        ageCell.textContent = responseData.age;

        var uuidCell = document.createElement('td');
        uuidCell.textContent = responseData.uuid;

        // Append the cells to the row
        newRow.appendChild(nameCell);
        newRow.appendChild(ageCell);
        newRow.appendChild(uuidCell);

        // Append the row to the table body
        tableBody.appendChild(newRow);

         }else{
            alert("Error:"+ xhr.statusText);
           document.getElementById('statusMessage').innerHTML="Failed to create student";
           document.getElementById('createStudent').disabled=false;
         }
      }
     };

   var requestBody=JSON.stringify({
      name:name,
      age:age
   });

   xhr.send(requestBody);



    
    } else{
     alert("age should not be allowed above 50");
    }
    }

    function deleteStudent(){
      var id=document.getElementById("id").value;

    }

    function updateStudent(){
      var id=document.getElementById("updateId").value;
      var name=document.getElementById("updateName").value;
      var age=document.getElementById("updateAge").value;
    }

    function getStudentId(){
      var id=document.getElementById("getId").value;
    }

    function showAddForm(){
      
    var contentDiv=document.getElementById("content");
    contentDiv.innerHTML= ` <h3>Add Student</h3>
                       <div class="form-group">
                        <lable for="name">Name:</lable>
                        <input type="text" class="form-control" id="name" required></input>
                        </div>

                        <div class="form-group">
                           <lable for="age">Age:</lable>
                           <input type="number" class="form-control" id="age" required></input>
                        </div>
                        <button type="button" id="createStudent" class="btn btn-primary" onclick="addStudent()">Add</button>
                        <div id="statusMessage"></div>
                        <table id="student-table">
                        <thead>
                          <tr>
                            <th>Name</th>
                            <th>Age</th>
                            <th>UUID</th>
                          </tr>
                        </thead>
                        <tbody id="student-table-body">
                          
                        </tbody>
                      </table>
                        `;

    }

    function showDeleteForm(){
      var contentDiv=document.getElementById("content");
      contentDiv.innerHTML=` <h3>Delete Student</h3>
                  <div class="form-group">
                     <lable for="id">ID:</lable>
                     <input type="text" class="form-control" id="id" required></input>
                  </div>

                  <button type="button" class="btn btn-danger" onclick="deleteStudent()">Delete</button>
      `;
    }

    function showUpdateForm(){
      var contentDiv=document.getElementById("content");

      contentDiv.innerHTML=` <h3>Update Student</h3>
                           <div class="form-group">
                              <lable for="updateId">ID:</lable>
                              <input type="text" class="form-control" id="updateId" required></input>
                           </div>

                           <div class="form-group">
                              <lable for="updateName">Name:</lable>
                              <input type="text" class="form-control" id="updateName" required>
                              </input>
                           </div>

                           <div class="form-group">
                              <lable for="updateAge">Age:</lable>
                              <input type="number" class="form-control" id="updateAge" required></input>
                           </div>
                           <button type="button" class="btn btn-primary" onclick="updateStudent()">Update</button>
      `;
    }

    function showGetForm(){

      var contentDiv=document.getElementById("content");
      contentDiv.innerHTML=` <h3>Get Student</h3>
                              <div class="form-group">
                                 <lable for="getId">ID:</lable>
                                 <input type="text" class="form-control" id="getId" required>

                                 </input>

                              </div>
                              <button type="button" class="btn btn-primary" onclick="getStudent()">Get</button>
      `;
    }


/*
     fetch("/students-details/",{
      method: "POST",
      headers:{
         "Content-Type": "application/jsob"
      },
      body: JSON.stringify({name:stuName,age:age})
   })
   .then(response =>{
      if(response.ok){
         
         return response.json();
      } 
      else{
         return Promise.reject(new Error("Error: "+response.statusText));

         
      }
   })
   .then(student =>{
      var studentInfo=document.getElementById("student-info");
      studentInfo.innerHTML=`<h3>Added Student:</h3>
                      <p>ID: ${student.id}</p>
                      <p>Name: ${student.name}</p>
                      <p>Age: ${student.age}</p>  `;
   })
   .catch(error =>{
      alert(error.message);
   });


*/


