**Secret**

Takes and input image and hides a text message from a file within the image, before saving this to a new output image.

Can also read a previously created secret image and retrieve the message.

**Example arguments**

To write a secret image:

`key="xyz123" image="small.jpg" message="message.txt" output="out.jpg" job="write"`

To read a secret image

`key="xyz123" image="out.jpg" output="read.txt" job="read"`

**Inspired By**

This program was inspired by a Computerphile (@computer_phile) video on stenanography (https://www.youtube.com/watch?v=TWEXCYQKyDc)