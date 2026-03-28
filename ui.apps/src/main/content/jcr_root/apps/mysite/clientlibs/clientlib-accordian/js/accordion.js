document.addEventListener("DOMContentLoaded", function () {

  const questions = document.querySelectorAll(".faq-question");

  questions.forEach(function (question) {
    question.addEventListener("click", function () {

      const item = this.parentElement;
      const answer = item.querySelector(".faq-answer");

      // Toggle current accordion
      item.classList.toggle("active");

       document.querySelectorAll(".faq-item").forEach(i => {
         if (i !== item) i.classList.remove("active");
       });

    });
  });

});
