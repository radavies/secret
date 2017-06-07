**Secret**

Takes an image as input and hides a text message, read from a file, within the image, before saving this to a new output image.

Can then read a previously created secret image and retrieve the message, writing it to an output text file.

The message is AES encrypted before being written into the image.

Currently works with JPG images.

Uses least significant bit steganography (using the two least significant bits).

**Example arguments**

To write a secret image:

`key="xyz123" image="small.jpg" message="message.txt" output="out.jpg" job="write"`

To read a secret image

`key="xyz123" image="out.jpg" output="read.txt" job="read"`

**Inspired By**

This program was inspired by a Computerphile (@computer_phile) video on stenanography (https://www.youtube.com/watch?v=TWEXCYQKyDc)