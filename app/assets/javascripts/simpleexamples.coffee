$ ->

# http://stackoverflow.com/questions/4214731/coffeescript-global-variables/4215132#4215132
root = exports ? this

root.publishIt = (data) ->
  console.log(data)
  $('#publish-out').append('<p style="color:green;">' + data + '</p>');

  